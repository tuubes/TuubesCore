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

import org.mcphoton.Photon;
import org.mcphoton.command.Command;
import org.mcphoton.command.GlobalCommandRegistry;
import org.mcphoton.command.WorldCommandRegistry;
import org.mcphoton.plugin.GlobalPlugin;
import org.mcphoton.world.World;

/**
 * Implementation of {@link GlobalCommandRegistry}. It internally uses the {@link
 * WorldCommandRegistry} of each world.
 *
 * @author TheElectronWill
 */
public class GlobalCommandRegistryImpl implements GlobalCommandRegistry {
	/**
	 * Registers a server command.
	 *
	 * @param cmd the command to register.
	 */
	public void registerInternalCommand(Command cmd) {
		for (World world : Photon.getServer().getWorlds()) {
			world.getCommandRegistry().registerCommand(cmd, null);
		}
	}

	@Override
	public void registerCommand(Command cmd, GlobalPlugin plugin) {
		for (World world : plugin.getActiveWorlds()) {
			world.getCommandRegistry().registerCommand(cmd, plugin);
		}
	}

	@Override
	public void unregisterCommand(Command cmd, GlobalPlugin plugin) {
		for (World world : plugin.getActiveWorlds()) {
			world.getCommandRegistry().unregisterCommand(cmd, plugin);
		}
	}
}