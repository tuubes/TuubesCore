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
package org.mcphoton.impl.entity;

import java.util.UUID;
import org.mcphoton.entity.living.Player;
import org.mcphoton.inventory.Inventory;
import org.mcphoton.world.Location;

/**
 *
 * @author TheElectronWill
 */
public class PlayerImpl implements Player {

	private final String name;
	private final UUID accoundId;
	private volatile Location location;

	public PlayerImpl(String name, UUID accoundId, Location location) {
		this.name = name;
		this.accoundId = accoundId;
		this.location = location;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public UUID getAccountId() {
		return accoundId;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public void setLocation(Location l) {
		this.location = l;
	}

	@Override
	public void setBanned(boolean b) {
	}

	@Override
	public void setOp(boolean b) {
	}

	@Override
	public void setWhitelisted(boolean b) {
	}

	@Override
	public boolean isBanned() {
		return false;
	}

	@Override
	public boolean hasPlayedBefore() {
		return false;
	}

	@Override
	public boolean isOnline() {
		return true;
	}

	@Override
	public boolean isOp() {
		return false;
	}

	@Override
	public boolean isWhitelisted() {
		return false;
	}

	@Override
	public Inventory getInventory() {
		return null;
	}

	@Override
	public boolean hasPermission(String permission) {
		return false;
	}

	@Override
	public String toString() {
		return "PhotonPlayer{" + "name=" + name + ", accoundId=" + accoundId + ", location=" + location + '}';
	}

}
