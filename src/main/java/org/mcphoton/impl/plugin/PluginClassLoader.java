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
		super(new URL[] {url});
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

}
