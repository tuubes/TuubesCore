/*
 * Copyright (c) 2016 MCPhoton <http://mcphoton.org> and contributors.
 *
 * This file is part of the Photon API <https://github.com/mcphoton/Photon-API>.
 *
 * The Photon API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Photon API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mcphoton.event.plugin;

import java.util.Collection;
import org.mcphoton.event.Event;
import org.mcphoton.plugin.Plugin;

/**
 * Event fired at startup, after all plugins are loaded.
 *
 * @author TheElectronWill
 *
 */
public class PluginsLoadedEvent implements Event {

	private final Collection<Plugin> loaded;

	public PluginsLoadedEvent(Collection<Plugin> loaded) {
		this.loaded = loaded;
	}

	/**
	 * Gets the plugins that were successfully loaded.
	 */
	public Collection<Plugin> getLoaded() {
		return loaded;
	}

}
