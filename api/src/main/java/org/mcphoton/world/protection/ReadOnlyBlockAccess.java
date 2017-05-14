package org.mcphoton.world.protection;

import org.mcphoton.block.BlockType;
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
	BlockType getBlockType(int x, int y, int z);

	/**
	 * @param loc the block's location (world is ignored).
	 * @return the block's data at the given location.
	 */
	default BlockType getBlockType(Location loc) {
		return getBlockType(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
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