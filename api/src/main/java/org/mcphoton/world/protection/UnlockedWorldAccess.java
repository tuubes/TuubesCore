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
import org.mcphoton.world.World;

/**
 * An unlocked access to an entire world.
 *
 * @author TheElectronWill
 */
public interface UnlockedWorldAccess extends ReadOnlyBlockAccess, QueryableBlockAccess {

	/**
	 * @return the world.
	 */
	World getWorld();

	/**
	 * Breaks a block.
	 *
	 * @param x the block's x coordinate.
	 * @param y the block's y coordinate.
	 * @param z the block's z coordinate.
	 * @return true in case of success.
	 */
	boolean breakBlock(int x, int y, int z);

	/**
	 * Breaks a block.
	 *
	 * @param loc the block's x location.
	 * @return true in case of success.
	 */
	default boolean breakBlock(Location loc) {
		return breakBlock(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	/**
	 * Sets a block.
	 *
	 * @param x the block's x coordinate.
	 * @param y the block's y coordinate.
	 * @param z the block's z coordinate.
	 * @return true in case of success.
	 */
	boolean setBlockData(int x, int y, int z, BlockData type);

	/**
	 * Sets a block.
	 *
	 * @param loc the block's x location.
	 * @return true in case of success.
	 */
	default boolean setBlockData(Location loc, BlockData type) {
		return setBlockData(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), type);
	}

	/**
	 * Sets a biome.
	 *
	 * @param x the biome's x coordinate.
	 * @param z the biome's z coordinate.
	 * @return true in case of success.
	 */
	boolean setBiomeType(int x, int z, BiomeType type);

	/**
	 * Sets a biome.
	 *
	 * @param loc the biome's location (the y coordinate and the world are ignored)s.
	 * @return true in case of success.
	 */
	default boolean setBiomeType(Location loc, BiomeType type) {
		return setBiomeType(loc.getBlockX(), loc.getBlockZ(), type);
	}

	/**
	 * Returns true.
	 */
	@Override
	public default boolean mayBreakBlock(int x, int y, int z, Object breaker) {
		return true;
	}

	/**
	 * Returns true.
	 */
	@Override
	public default boolean maySetBlockData(int x, int y, int z, BlockData data, Object setter) {
		return true;
	}

	/**
	 * Returns true.
	 */
	@Override
	public default boolean maySetBiomeType(int x, int z, BiomeType type, Object setter) {
		return true;
	}

}
