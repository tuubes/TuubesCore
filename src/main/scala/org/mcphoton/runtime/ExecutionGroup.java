package org.mcphoton.runtime;

import com.electronwill.utils.Bag;
import com.electronwill.utils.SimpleBag;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.mcphoton.Photon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TheElectronWill
 */
public final class ExecutionGroup implements ExecutionContext, Runnable {
	private static final Logger logger = LoggerFactory.getLogger(ExecutionGroup.class);
	/**
	 * Max number of tasks from the queue to execute at each loop run.
	 */
	private static final int MAX_TASK_EXECUTION = 1000;//TODO choose a better limit, time-based perhaps?
	/**
	 * The period between each execution, in milliseconds.
	 */
	public static final long UPDATE_PERIOD = 100;//TODO make it configurable?

	/**
	 * Executes a task in the right ExecutionContext. This method is thread-safe.
	 *
	 * @param task           the task to execute
	 * @param taskContext    the context in which the task must be executed
	 * @param currentContext the current context
	 */
	public static void safeExecute(Runnable task, ExecutionContext taskContext,
								   ExecutionContext currentContext) {
		if (currentContext == taskContext) {
			task.run();
		} else {
			((ExecutionGroup)taskContext).enqueueTask(task);
		}
	}

	/**
	 * Executes a task in the right ExecutionContext. This method is thread-safe.
	 *
	 * @param task           the task to execute
	 * @param object         the object on which the task operates
	 * @param currentContext the current context
	 */
	public static void safeExecute(Runnable task, ContextBound object,
								   ExecutionContext currentContext) {
		safeExecute(task, object.getAssociatedContext(), currentContext);
	}

	/**
	 * Contains the tasks to run the next time.
	 */
	private final Queue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();
	/**
	 * Contains the Updatable objects to update every time.
	 */
	private final Bag<Updatable> updatableBag = new SimpleBag<>();
	/**
	 * The ScheduledFuture created when the ExecutionGroup is scheduled with the server's
	 * ExecutorService.
	 */
	private volatile ScheduledFuture<?> scheduledFuture;
	/**
	 * The instant of the previous loop run. Used to calculate the deltaTime.
	 */
	private long previousTime;

	/**
	 * Adds a task to the task queue. This method is thread-safe.
	 *
	 * @param task the task to enqueue
	 */
	public void enqueueTask(Runnable task) {
		taskQueue.add(task);
	}

	/**
	 * Adds an Updatable to this group. This method must be called in this ExecutionGroup.
	 *
	 * @param updatable the Updatable to add
	 */
	public void addUpdatable(Updatable updatable) {
		updatableBag.add(updatable);
	}

	/**
	 * Removes an Updatable from this group. This method must be called in this ExecutionGroup.
	 *
	 * @param updatable the Updatable to remove
	 */
	public void removeUpdatable(Updatable updatable) {
		updatableBag.remove(updatable);
	}

	/**
	 * Starts this ExecutionGroup with the ScheduledExecutorService.
	 */
	public void start() {
		if (scheduledFuture != null) {
			return;// Task already scheduled
		}
		previousTime = System.nanoTime();
		scheduledFuture = Photon.getExecutorService()
								.scheduleAtFixedRate(this, UPDATE_PERIOD, UPDATE_PERIOD,
													 TimeUnit.MILLISECONDS);
	}

	/**
	 * Stops this ExecutionGroup as soon as possible.
	 */
	public void stop() {
		if (scheduledFuture == null) {
			return;// Task not scheduled yet
		}
		scheduledFuture.cancel(false);
	}

	@Override
	public void run() {
		// Executes up to MAX_TASK_EXECUTION tasks
		int executed = 1;
		Runnable task;
		for (; executed <= MAX_TASK_EXECUTION && (task = taskQueue.poll()) != null; executed++) {
			try {
				task.run();
			} catch (Exception e) {
				logger.error("An exception occured while executing task {}.", task, e);
			}
		}
		if (executed == MAX_TASK_EXECUTION) {
			logger.warn("Task execution limit ({}) reached!", MAX_TASK_EXECUTION);
		}
		// Stops here if the task is cancelled
		if (scheduledFuture.isCancelled()) {
			return;
		}
		// Calculates the deltaTime (elapsed time since the last update)
		long newTime = System.nanoTime();
		long deltaTime = newTime - previousTime;
		previousTime = newTime;
		double dt = deltaTime / 1_000_000_000.0;// in seconds
		// Updates the updatables objects with the deltaTime
		for (Iterator<Updatable> iterator = updatableBag.iterator(); iterator.hasNext(); ) {
			Updatable updatable = iterator.next();
			try {
				updatable.update(dt);
			} catch (Exception e) {
				logger.error("An exception occured while updating object {}. It won't be "
							 + "updated anymore.", updatable, e);
				iterator.remove();
				try {
					updatable.destroy();
				} catch (Exception e1) {
					logger.error("Unable to destroy the Updatable {}.");
				}
			}
		}
		// Send the necessary packets to the near clients
		for (Iterator<Updatable> iterator = updatableBag.iterator(); iterator.hasNext(); ) {
			Updatable updatable = iterator.next();
			try {
				updatable.sendUpdates();
			} catch (Exception e) {
				logger.error(
						"An exception occured while sending the update of {} to the clients" + ".",
						updatable, e);
			}
		}
	}
}