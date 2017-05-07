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
import org.mcphoton.messaging.Messageable;

public class ListCommand implements Command {
	@Override
	public void execute(Messageable source, String[] args) {

		switch (Photon.getServer().getOnlinePlayers().size()) {
			case 0:
				source.sendMessage("No player is connected.");
				break;
			case 1:
				source.sendMessage("There is 1 out of "
								   + Photon.getServer().getConfiguration().getMaxPlayers()
								   + " player connected.");
				break;
			default:
				source.sendMessage("There are "
								   + Photon.getServer().getOnlinePlayers().size()
								   + " out of "
								   + Photon.getServer().getConfiguration().getMaxPlayers()
								   + " players connected.");
				break;
		}
	}

	@Override
	public String getDescription() {
		return "Lists the connected players.";
	}

	@Override
	public String getName() {
		return "list";
	}
}