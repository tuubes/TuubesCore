package org.mcphoton.plugin;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.atomic.AtomicInteger;
import org.mcphoton.plugin.ClassSharer;
import org.mcphoton.plugin.SharedClassLoader;

public final class PluginClassLoader extends URLClassLoader implements SharedClassLoader {

	private final ClassSharer sharer;
	private final AtomicInteger useCount = new AtomicInteger();

	public PluginClassLoader(URL[] urls, ClassSharer sharer) {
		super(urls);
		this.sharer = sharer;
	}

	public PluginClassLoader(URL url, ClassSharer sharer) {
		super(new URL[] {url});
		this.sharer = sharer;
	}

	@Override
	public int decreaseUseCount() {
		return useCount.getAndDecrement();
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		return findClass(name, true);
	}

	@Override
	public Class<?> findClass(String name, boolean checkShared) throws ClassNotFoundException {
		if (checkShared) {
			Class<?> c = sharer.getClass(name);
			if (c != null) {
				return c;
			}
		}
		return super.findClass(name);

	}

	@Override
	public ClassSharer getSharer() {
		return sharer;
	}

	@Override
	public int getUseCount() {
		return useCount.get();
	}

	@Override
	public int increaseUseCount() {
		return useCount.getAndIncrement();
	}

}
