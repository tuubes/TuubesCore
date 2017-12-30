package org.tuubes.core.tasks;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author TheElectronWill
 */
public final class TaskSystem {
	private TaskSystem() {}

	static final ScheduledExecutorService executor;

	static {
		ThreadFactory factory = new CountingThreadFactory("TaskSystem-");
		int threadNumber = Runtime.getRuntime().availableProcessors(); // TODO configure
		executor = Executors.newScheduledThreadPool(threadNumber, factory);
	}

	public static CancellableTask execute(Runnable task) {
		return new CancellableFuture<>(executor.submit(task));
	}

	public static DelayedTask schedule(Runnable command, long delay, TimeUnit unit) {
		return new DelayedFuture(executor.schedule(command, delay, unit));
	}

	public static DelayedTask scheduleAtFixedRate(Runnable command, long initialDelay,
																	 long period, TimeUnit unit) {
		return new DelayedFuture(
				executor.scheduleAtFixedRate(command, initialDelay, period, unit));
	}

	public static DelayedTask scheduleWithFixedDelay(Runnable command, long initialDelay,
													 long delay, TimeUnit unit) {
		return new DelayedFuture(
				executor.scheduleWithFixedDelay(command, initialDelay, delay, unit));
	}
}