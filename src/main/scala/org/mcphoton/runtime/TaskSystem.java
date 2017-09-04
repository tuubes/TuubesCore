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
		ThreadFactory factory = new ThreadFactory() {
			private final AtomicInteger count = new AtomicInteger();

			@Override
			public Thread newThread(Runnable runnable) {
				return new Thread("TaskThread-" + count.getAndIncrement());
			}
		};
		executor = Executors.newScheduledThreadPool(0, factory);
	}

	public static void execute(Runnable task) {
		executor.execute(task);
	}

	public static void schedule(Runnable command, long delay, TimeUnit unit) {
		executor.schedule(command, delay, unit);
	}

	public static void scheduleAtFixedRate(Runnable command, long initialDelay, long period,
										   TimeUnit unit) {
		executor.scheduleAtFixedRate(command, initialDelay, period, unit);
	}

	public static void scheduleWithFixedDelay(Runnable command, long initialDelay, long delay,
											  TimeUnit unit) {
		executor.scheduleWithFixedDelay(command, initialDelay, delay, unit);
	}
}