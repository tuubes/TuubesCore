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
package org.mcphoton.world.protection;

import org.mcphoton.block.BlockData;
import org.mcphoton.utils.Location;
import org.mcphoton.world.BiomeType;

/**
 * A QueryableBlockAccess provides methods to check if a specific action is allowed. Like the other types of
 * block accesses, it is specific to a world or area.
 *
 * @author TheElectronWill
 */
public interface QueryableBlockAccess {

	/**
	 * Checks if a block may be broken.
	 *
	 * @param x the block's x coordinate.
	 * @param y the block's y coordinate.
	 * @param z the block's z coordinate.
	 * @param breaker the block's breaker.
	 * @return true if it may be broken, false otherwise.
	 */
	boolean mayBreakBlock(int x, int y, int z, Object breaker);

	/**
	 * Checks if a block may be broken.
	 *
	 * @param x the block's x coordinate.
	 * @param y the block's y coordinate.
	 * @param z the block's z coordinate.
	 * @param breaker the block's breaker.
	 * @return true if it may be broken, false otherwise.
	 */
	default boolean mayBreakBlock(Location loc, Object breaker) {
		return mayBreakBlock(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), breaker);
	}

	/**
	 * Checks if a block may be set.
	 *
	 * @param x the block's x coordinate.
	 * @param y the block's y coordinate.
	 * @param z the block's z coordinate.
	 * @param data the data to set.
	 * @param setter the block's setter.
	 * @return true if it may be set, false otherwise.
	 */
	boolean maySetBlockData(int x, int y, int z, BlockData data, Object setter);

	/**
	 * Checks if a block may be set.
	 *
	 * @param loc the block's x location.
	 * @param data the data to set.
	 * @param setter the block's setter.
	 * @return true if it may be set, false otherwise.
	 */
	default boolean maySetBlockData(Location loc, BlockData data, Object setter) {
		return maySetBlockData(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), data, setter);
	}

	/**
	 * Checks if a biome may be set.
	 *
	 * @param x the biome's x coordinate.
	 * @param z the biome's z coordinate.
	 * @param setter the biome's setter.
	 * @return true if it may be set, false otherwise.
	 */
	boolean maySetBiomeType(int x, int z, BiomeType type, Object setter);

	/**
	 * Checks if a biome may be set.
	 *
	 * @param loc the biome's location.
	 * @param setter the biome's setter.
	 * @return true if it may be set, false otherwise.
	 */
	default boolean maySetBiomeType(Location loc, BiomeType type, Object setter) {
		return maySetBiomeType(loc.getBlockX(), loc.getBlockZ(), type, setter);
	}

}
