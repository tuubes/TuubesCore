package org.mcphoton.impl.plugin;

import com.electronwill.utils.SimpleBag;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.mcphoton.plugin.ClassSharer;
import org.mcphoton.plugin.SharedClassLoader;

/**
 * Implementation of ClassSharer.
 *
 * @author TheElectronWill
 */
public final class ClassSharerImpl implements ClassSharer {

	private final Map<String, Class<?>> classMap = new HashMap<>();
	private final Collection<SharedClassLoader> sharedClassLoaders = new SimpleBag<>();

	@Override
	public synchronized Class<?> getClass(String name) {
		Class<?> c = classMap.get(name);
		if (c != null) {
			// TODO vérifier si on a vraiment besoin d'un HashMap: si classe déjà chargée la JVM le sait et la renvoie
			// directement avec findLoadedClass() (voir ClassLoader.findClass(name))?
			System.out.println("classMap returned " + c);
			return c;
		}
		for (SharedClassLoader classLoader : sharedClassLoaders) {
			try {
				c = classLoader.findClass(name, false);
			} catch (ClassNotFoundException e) {
				// ignore
			}
			if (c != null) {
				classMap.put(name, c);
				return c;
			}
		}
		return c;
	}

	@Override
	public synchronized void addClassLoader(SharedClassLoader classLoader) {
		sharedClassLoaders.add(classLoader);
	}

	@Override
	public synchronized void removeClassLoader(SharedClassLoader classLoader) {
		sharedClassLoaders.remove(classLoader);
	}

	@Override
	public void removeUselessClassLoader(SharedClassLoader classLoader) {
		if (classLoader.getUseCount() <= 0) {
			sharedClassLoaders.remove(classLoader);
		}
	}

}
