package org.mcphoton.runtime;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author TheElectronWill
 */
public final class TaskSystem {
	private TaskSystem() {}

	static final ScheduledExecutorService executor;

	static {
		ThreadFactory factory = new CountingThreadFactory("TaskThread_");
		executor = Executors.newScheduledThreadPool(0, factory);
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