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
	default boolean mayBreakBlock(int x, int y, int z, Object breaker) {
		return true;
	}

	/**
	 * Returns true.
	 */
	@Override
	default boolean maySetBlockData(int x, int y, int z, BlockData data, Object setter) {
		return true;
	}

	/**
	 * Returns true.
	 */
	@Override
	default boolean maySetBiomeType(int x, int z, BiomeType type, Object setter) {
		return true;
	}
}