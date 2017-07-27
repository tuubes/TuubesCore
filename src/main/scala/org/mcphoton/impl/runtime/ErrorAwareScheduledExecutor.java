package org.mcphoton.impl.runtime;

import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A ScheduledThreadPoolExecutor that catches the tasks' exeptions to log them.
 *
 * @author TheElectronWill
 */
public final class ErrorAwareScheduledExecutor extends ScheduledThreadPoolExecutor {
	private static final Logger logger = LoggerFactory.getLogger("ScheduledExecutorService");

	public ErrorAwareScheduledExecutor(int corePoolSize) {
		super(corePoolSize, new ExecutionThreadFactory());
	}

	@Override
	protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable,
														  RunnableScheduledFuture<V> task) {
		return new ErrorAwareScheduledFuture<>(super.decorateTask(runnable, task));
	}

	@Override
	protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> callable,
														  RunnableScheduledFuture<V> task) {
		return new ErrorAwareScheduledFuture<>(super.decorateTask(callable, task));
	}

	private static final class ErrorAwareScheduledFuture<V> implements RunnableScheduledFuture<V> {
		private final RunnableScheduledFuture<V> future;

		private ErrorAwareScheduledFuture(RunnableScheduledFuture<V> future) {this.future = future;}

		@Override
		public boolean isPeriodic() {
			return future.isPeriodic();
		}

		@Override
		public long getDelay(TimeUnit unit) {
			return future.getDelay(unit);
		}

		@Override
		public int compareTo(Delayed o) {
			return future.compareTo(o);
		}

		@Override
		public void run() {
			try {
				future.run();
			} catch (Throwable t) {
				// Logs and rethrows so the ScheduledExecutorService can cancel the task
				logger.error("Error in task execution.", t);
				throw t;
			}
		}

		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			return future.cancel(mayInterruptIfRunning);
		}

		@Override
		public boolean isCancelled() {
			return future.isCancelled();
		}

		@Override
		public boolean isDone() {
			return future.isDone();
		}

		@Override
		public V get() throws InterruptedException, ExecutionException {
			return future.get();
		}

		@Override
		public V get(long timeout, TimeUnit unit)
				throws InterruptedException, ExecutionException, TimeoutException {
			return future.get(timeout, unit);
		}
	}

	static final class ExecutionThreadFactory implements ThreadFactory {
		private final AtomicInteger count = new AtomicInteger(1);

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "executor-" + count.getAndIncrement());
		}
	}
}