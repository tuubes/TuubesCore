package org.mcphoton.impl.server;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author TheElectronWill
 */
final class ExecutionThreadFactory implements ThreadFactory {

	private final AtomicInteger count = new AtomicInteger(1);

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(r, "executor-" + count.getAndIncrement());
	}

}
