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
 * A checked world access. Every action is checked.
 *
 * @author TheElectronWill
 */
public interface CheckedWorldAccess extends ReadOnlyBlockAccess, QueryableBlockAccess {

	/**
	 * Breaks a block.
	 *
	 * @param x the block's x coordinate.
	 * @param y the block's y coordinate.
	 * @param z the block's z coordinate.
	 * @param breaker the block's breaker.
	 * @return true if it has been broken, false otherwise.
	 */
	boolean breakBlock(int x, int y, int z, Object breaker);

	/**
	 * Breaks a block.
	 *
	 * @param loc the block's location.
	 * @param breaker the block's breaker.
	 * @return true if it has been broken, false otherwise.
	 */
	default boolean breakBlock(Location loc, Object breaker) {
		return breakBlock(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), breaker);
	}

	/**
	 * Sets a block.
	 *
	 * @param x the block's x coordinate.
	 * @param y the block's y coordinate.
	 * @param z the block's z coordinate.
	 * @param setter the block's setter.
	 * @return true if it has been set, false otherwise.
	 */
	boolean setBlockData(int x, int y, int z, BlockData type, Object setter);

	/**
	 * Sets a block.
	 *
	 * @param loc the block's location.
	 * @param setter the block's setter.
	 * @return true if it has been set, false otherwise.
	 */
	default boolean setBlockData(Location loc, BlockData type, Object setter) {
		return setBlockData(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), type, setter);
	}

	/**
	 * Sets a biome.
	 *
	 * @param x the biome's x coordinate.
	 * @param z the biome's z coordinate.
	 * @param setter the block's setter.
	 * @return true if it has been broken, false otherwise.
	 */
	boolean setBiomeType(int x, int z, BiomeType type, Object setter);

	/**
	 * Sets a biome.
	 *
	 * @param x the biome's x location (the y coordinate and the world are ignored).
	 * @param z the block's z coordinate.
	 * @param setter the block's setter.
	 * @return true if it has been set, false otherwise.
	 */
	default boolean setBiomeType(Location loc, BiomeType type, Object setter) {
		return setBiomeType(loc.getBlockX(), loc.getBlockZ(), type, setter);
	}

}
