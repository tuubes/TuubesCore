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
package org.mcphoton.world.areas;

import org.mcphoton.utils.Location;
import org.mcphoton.world.World;

/**
 * A 3D area that contains blocks.
 *
 * @author TheElectronWill
 */
public interface Area extends Iterable<Location> {

	/**
	 * Checks if the given coordinates are inside this area.
	 */
	boolean contains(int x, int y, int z);

	/**
	 * Checks if the given location is inside this area.
	 */
	default boolean contains(Location loc) {
		return loc.getWorld() == getWorld() && contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	/**
	 * Gets the area's world.
	 */
	World getWorld();

	/**
	 * Gets the area's size, in blocks (empty blocks are counted).
	 */
	int size();

}
