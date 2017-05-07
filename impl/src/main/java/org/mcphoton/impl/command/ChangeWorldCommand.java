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
import org.mcphoton.impl.server.ConsoleThread;
import org.mcphoton.messaging.Messageable;
import org.mcphoton.world.World;

/**
 * Changes the world where the commands are executed. Similar to the "cd" command, but for worlds.
 *
 * @author TheElectronWill
 */
public class ChangeWorldCommand implements Command {
	private static final String[] ALIASES = {"cw"};

	@Override
	public void execute(Messageable source, String[] args) {
		if (args.length != 1) {
			source.sendMessage("Invalid syntax. Use cw [world]");
			return;
		}
		World world = Photon.getServer().getWorld(args[0]);
		if (world == null) {
			source.sendMessage("This world does not exist.");
			return;
		}
		if (source instanceof ConsoleThread) {
			Photon.getServer().getConsoleThread().world = world;
		} else {
			source.sendMessage("This command only works in the server's console!");
		}
	}

	@Override
	public String getDescription() {
		return "Changes the world where the console commands are executed. Only works in the server's console.";
	}

	@Override
	public String getName() {
		return "change-world";
	}

	@Override
	public String[] getAliases() {
		return ALIASES;
	}
}