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
 * A ReadyOnlyBlockAccess provides method to get informations about blocks. Like the other types of
 * block accesses, it is specific to a world or area.
 *
 * @author TheElectronWill
 */
public interface ReadOnlyBlockAccess {

	/**
	 * @param x the block's x coordinate
	 * @param y the block's y coordinate
	 * @param z the block's z coordinate
	 * @return the block's data at the given location.
	 */
	BlockData getBlockData(int x, int y, int z);

	/**
	 * @param loc the block's location (world is ignored).
	 * @return the block's data at the given location.
	 */
	default BlockData getBlockData(Location loc) {
		return getBlockData(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}

	/**
	 * @param x the biome's x coordinate
	 * @param z the biome's z coordinate
	 * @return the biome's type at the given location.
	 */
	BiomeType getBiomeType(int x, int z);

	/**
	 * @param loc the biome's location (world is ignored).
	 * @return the biome's type at the given location.
	 */
	default BiomeType getBiomeType(Location loc) {
		return getBiomeType(loc.getBlockX(), loc.getBlockZ());
	}

}
