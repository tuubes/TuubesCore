package org.mcphoton.impl.plugin;

import com.electronwill.utils.SimpleBag;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.mcphoton.plugin.ClassSharer;
import org.mcphoton.plugin.SharedClassLoader;

/**
 * Shares classes across plugins.
 *
 * @author TheElectronWill
 *
 */
public class PhotonClassSharer implements ClassSharer {

	private final Map<String, Class<?>> classMap = new HashMap<>();
	private final Collection<SharedClassLoader> sharedFinders = new SimpleBag<>();

	@Override
	public synchronized Class<?> getClass(String name) {
		Class<?> c = classMap.get(name);
		if (c != null) {
			// TODO vérifier si on a vraiment besoin d'un HashMap: si classe déjà chargée la JVM le sait et la renvoie
			// directement avec findLoadedClass() (voir ClassLoader.findClass(name))?
			System.out.println("classMap returned " + c);
			return c;
		}
		for (SharedClassLoader finder : sharedFinders) {
			try {
				c = finder.findClass(name, false);
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
	public synchronized void addClassLoader(SharedClassLoader finder) {
		sharedFinders.add(finder);
	}

	@Override
	public synchronized void removeClassLoader(SharedClassLoader finder) {
		sharedFinders.remove(finder);
	}

}
