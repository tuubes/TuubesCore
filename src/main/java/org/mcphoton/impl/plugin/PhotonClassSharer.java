/* 
 * Copyright (c) 2016 MCPhoton <http://mcphoton.org> and contributors.
 * 
 * This file is part of the Photon Server Implementation <https://github.com/mcphoton/Photon-Server>.
 * 
 * The Photon Server Implementation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Photon Server Implementation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
