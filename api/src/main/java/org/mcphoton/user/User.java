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
package org.mcphoton.user;

import java.util.UUID;
import org.mcphoton.entity.living.Player;
import org.mcphoton.inventory.InventoryHolder;
import org.mcphoton.permissions.Permissible;
import org.mcphoton.utils.Location;

/**
 * Represents the informations that the Photon server has about a particular user.
 *
 * @author TheElectronWill
 */
public interface User extends Permissible, InventoryHolder {
	/**
	 * @return the user's name.
	 */
	String getName();

	/**
	 * @return the user's (unique) account id.
	 */
	UUID getAccountId();

	/**
	 * Gets the user's location. If they are currently connected, returns their current location.
	 * If they aren't connected, returns the last known location.
	 *
	 * @return the user's location.
	 */
	Location getLocation();

	/**
	 * Sets the user's location. If they are currenctly connected, teleport them. If not, set the
	 * location they will spawn at when they reconnect.
	 *
	 * @param l the location to set.
	 */
	void setLocation(Location l);

	/**
	 * Checks if this user is currently connected.
	 *
	 * @return {@code true} if the user is online
	 */
	boolean isOnline();

	/**
	 * Returns this user as a {@link Player} instance, if connected.
	 *
	 * @return the Player instance that corresponds to the user.
	 *
	 * @throws IllegalStateException if the user isn't connected.
	 */
	Player asPlayer();
}