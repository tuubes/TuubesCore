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

import org.mcphoton.entity.living.Player;
import org.mcphoton.inventory.InventoryHolder;
import org.mcphoton.permissions.Permissible;
import org.mcphoton.utils.Location;

import java.util.UUID;

/**
 * Represents the informations that the Photon server has about a particular user.
 *
 * @author TheElectronWill
 * @author DJmaxZPLAY
 */
public interface User extends Permissible, InventoryHolder {

	/**
	 * Gets the user's name, which can be changed by the user.
	 *
	 * @return the user's name.
	 */
	String getName();

	/**
	 * Gets the user's account id, which uniquely identifies this user.
	 *
	 * @return the user's account id.
	 */
	UUID getAccountId();

	/**
	 * Gets the user's location. If the user is currently connected, this method returns his/her current
	 * location. If he/she isn't connected, this method returns the last known location of the user; where
	 * he/she will spawn the next time he/she connects.
	 *
	 * @return the user's location.
	 */
	Location getLocation();

	/**
	 * Sets the user's location. If the user is currently connected, this method teleports him/her. If he/she
	 * isn't connected, this method modifies the server's data so that the user will spawn at this position
	 * the next time he/she connects.
	 *
	 * @param l the location to set.
	 */
	void setLocation(Location l);

	/**
	 * Checks if this user is currently connected.
	 *
	 * @return {@code true} if the user is connected, else {@code false}.
	 */
	boolean isConnected();

	/**
	 * Returns this user as a {@link Player} instance, if he/she is connected to the server.
	 *
	 * @return the Player instance that corresponds to the user.
	 * @throws IllegalStateException if the user isn't connected.
	 */
	Player asPlayer();
}
