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
package org.mcphoton.impl.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.mcphoton.command.Command;
import org.mcphoton.command.WorldCommandRegistry;
import org.mcphoton.plugin.Plugin;

/**
 * Implementation of {@link WorldCommandRegistry}.
 *
 * @author TheElectronWill
 */
public class WorldCommandRegistryImpl implements WorldCommandRegistry {

	private final Map<String, Command> nameMap = new HashMap<>();
	private final Map<Plugin, List<Command>> pluginMap = new HashMap<>();

	@Override
	public synchronized void register(Command cmd, Plugin plugin) {
		nameMap.put(cmd.getName(), cmd);
		List<Command> list = pluginMap.get(plugin);
		if (list == null) {
			list = new LinkedList<>();
			pluginMap.put(plugin, list);
		}
		list.add(cmd);
		for (String alias : cmd.getAliases()) {
			nameMap.putIfAbsent(alias, cmd);
		}
	}

	@Override
	public synchronized void unregister(Command cmd, Plugin plugin) {
		nameMap.remove(cmd.getName(), cmd);
		List<Command> list = pluginMap.get(plugin);
		if (list != null) {
			list.remove(cmd);
		}
		for (String alias : cmd.getAliases()) {
			nameMap.remove(alias, cmd);
		}
	}

	@Override
	public synchronized Command getRegistered(String cmdName) {
		return nameMap.get(cmdName);
	}

	@Override
	public synchronized List<Command> getAllRegistered(Plugin plugin) {
		List list = pluginMap.get(plugin);
		return list == null ? null : Collections.unmodifiableList(list);
	}

}
