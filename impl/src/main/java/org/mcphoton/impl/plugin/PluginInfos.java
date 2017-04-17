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
import java.util.Collections;
import org.mcphoton.plugin.Plugin;
import org.mcphoton.plugin.PluginDescription;
import org.mcphoton.world.World;

/**
 *
 * @author TheElectronWill
 */
public class PluginInfos {

	public final Class<? extends Plugin> clazz;
	public final PluginClassLoader classLoader;
	public final PluginDescription description;
	public Collection<World> worlds;

	public PluginInfos(Class<? extends Plugin> clazz, PluginClassLoader classLoader, PluginDescription description) {
		this.clazz = clazz;
		this.classLoader = classLoader;
		this.description = description;
	}

	public Collection<World> getWorlds() {
		if (worlds == null) {
			worlds = Collections.synchronizedCollection(new SimpleBag<>()); //synchronized because any thread could use it
		}
		return worlds;
	}

	public void setWorlds(Collection<World> worlds) {
		Collection<World> copy = new SimpleBag<>(worlds.size(), 1);
		copy.addAll(worlds); //copy the data so that each plugin gets its own list of world.
		this.worlds = Collections.synchronizedCollection(copy); //synchronized because any thread could use it
	}

}
