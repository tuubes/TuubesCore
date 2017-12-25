package org.tuubes.runtime;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author TheElectronWill
 */
final class CountingThreadFactory implements ThreadFactory {
	private final String prefix;
	private final AtomicInteger count = new AtomicInteger();

	CountingThreadFactory(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public Thread newThread(Runnable runnable) {
		return new Thread(prefix + count.getAndIncrement());
	}
}