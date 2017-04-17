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
package org.mcphoton.server;

import java.util.Collection;
import org.mcphoton.entity.living.Player;
import org.mcphoton.world.World;

/**
 * Represents a game server.
 */
public interface Server {

	/**
	 * @return the server's BansManager.
	 */
	BansManager getBansManager();

	/**
	 * @return the server's WhitelistManager.
	 */
	WhitelistManager getWhitelistManager();

	/**
	 * Gets a collection containing all the currently connected players. The returned collection isn't
	 * modifiable.
	 *
	 * @return the online players.
	 */
	Collection<Player> getOnlinePlayers();

	/**
	 * Gets a collection containing all the server's worlds. The returned collection isn't modifiable.
	 *
	 * @return the server's worlds.
	 */
	Collection<World> getWorlds();

	/**
	 * Gets the world with the specified name.
	 *
	 * @param name the world's name.
	 * @return the world with the specified name.
	 */
	World getWorld(String name);

	/**
	 * @return the server's configuration.
	 */
	ServerConfiguration getConfiguration();

	/**
	 * @return the server's implementation version.
	 */
	String getVersion();

}
