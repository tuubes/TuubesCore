package org.mcphoton.impl.plugin;

import java.net.URL;
import java.net.URLClassLoader;
import org.mcphoton.plugin.ClassSharer;
import org.mcphoton.plugin.SharedClassLoader;

public class PluginClassLoader extends URLClassLoader implements SharedClassLoader {
	
	private final PhotonClassSharer sharer;
	
	public PluginClassLoader(URL[] urls, PhotonClassSharer sharer) {
		super(urls);
		this.sharer = sharer;
	}
	
	public PluginClassLoader(URL url, PhotonClassSharer sharer) {
		super(new URL[] { url });
		this.sharer = sharer;
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		return findClass(name, true);
	}
	
	@Override
	public Class<?> findClass(String name, boolean checkShared) throws ClassNotFoundException {
		if (checkShared) {
			Class<?> c = sharer.getClass(name);
			if (c != null)
				return c;
		}
		return super.findClass(name);
		
	}
	
	@Override
	public ClassSharer getSharer() {
		return sharer;
	}
	
}
