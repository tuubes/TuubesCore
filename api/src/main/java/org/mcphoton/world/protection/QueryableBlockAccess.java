package org.mcphoton.world.protection;

import org.mcphoton.block.BlockType;
import org.mcphoton.utils.Location;
import org.mcphoton.world.BiomeType;

/**
 * A QueryableBlockAccess provides methods to check if a specific action is allowed. Like the other
 * types of block accesses, it is specific to a world or area.
 *
 * @author TheElectronWill
 */
public interface QueryableBlockAccess {
	/**
	 * Checks if a block may be broken.
	 *
	 * @param x       the block's x coordinate.
	 * @param y       the block's y coordinate.
	 * @param z       the block's z coordinate.
	 * @param breaker the block's breaker.
	 * @return {@code true} if it may be broken, false otherwise.
	 */
	boolean mayBreakBlock(int x, int y, int z, Object breaker);

	/**
	 * Checks if a block may be broken.
	 *
	 * @param loc     the block's coordinates (the world is ignored).
	 * @param breaker the block's breaker.
	 * @return {@code true} if it may be broken, false otherwise.
	 */
	default boolean mayBreakBlock(Location loc, Object breaker) {
		return mayBreakBlock(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), breaker);
	}

	/**
	 * Checks if a block may be set.
	 *
	 * @param x      the block's x coordinate.
	 * @param y      the block's y coordinate.
	 * @param z      the block's z coordinate.
	 * @param type   the type to set.
	 * @param setter the block's setter.
	 * @return {@code true} if it may be set, false otherwise.
	 */
	boolean maySetBlockType(int x, int y, int z, BlockType type, Object setter);

	/**
	 * Checks if a block may be set.
	 *
	 * @param loc    the block's x location.
	 * @param type   the type to set.
	 * @param setter the block's setter.
	 * @return {@code true} if it may be set, false otherwise.
	 */
	default boolean maySetBlockType(Location loc, BlockType type, Object setter) {
		return maySetBlockType(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), type, setter);
	}

	/**
	 * Checks if a biome may be set.
	 *
	 * @param x      the biome's x coordinate.
	 * @param z      the biome's z coordinate.
	 * @param setter the biome's setter.
	 * @return {@code true} if it may be set, false otherwise.
	 */
	boolean maySetBiomeType(int x, int z, BiomeType type, Object setter);

	/**
	 * Checks if a biome may be set.
	 *
	 * @param loc    the biome's location.
	 * @param setter the biome's setter.
	 * @return {@code true} if it may be set, false otherwise.
	 */
	default boolean maySetBiomeType(Location loc, BiomeType type, Object setter) {
		return maySetBiomeType(loc.getBlockX(), loc.getBlockZ(), type, setter);
	}
}