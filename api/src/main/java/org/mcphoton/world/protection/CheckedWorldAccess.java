package org.mcphoton.world.protection;

import org.mcphoton.block.BlockType;
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
	 * @param x       the block's x coordinate.
	 * @param y       the block's y coordinate.
	 * @param z       the block's z coordinate.
	 * @param breaker the block's breaker.
	 * @return true if it has been broken, false otherwise.
	 */
	boolean breakBlock(int x, int y, int z, Object breaker);

	/**
	 * Breaks a block.
	 *
	 * @param loc     the block's location.
	 * @param breaker the block's breaker.
	 * @return true if it has been broken, false otherwise.
	 */
	default boolean breakBlock(Location loc, Object breaker) {
		return breakBlock(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), breaker);
	}

	/**
	 * Sets a block.
	 *
	 * @param x      the block's x coordinate.
	 * @param y      the block's y coordinate.
	 * @param z      the block's z coordinate.
	 * @param setter the block's setter.
	 * @return true if it has been set, false otherwise.
	 */
	boolean setBlockType(int x, int y, int z, BlockType type, Object setter);

	/**
	 * Sets a block.
	 *
	 * @param loc    the block's location.
	 * @param setter the block's setter.
	 * @return true if it has been set, false otherwise.
	 */
	default boolean setBlockType(Location loc, BlockType type, Object setter) {
		return setBlockType(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), type, setter);
	}

	/**
	 * Sets a biome.
	 *
	 * @param x      the biome's x coordinate.
	 * @param z      the biome's z coordinate.
	 * @param type   the new biome type
	 * @param setter the block's setter.
	 * @return true if it has been broken, false otherwise.
	 */
	boolean setBiomeType(int x, int z, BiomeType type, Object setter);

	/**
	 * Sets a biome.
	 *
	 * @param loc    the biome's x location (the y coordinate and the world are ignored).
	 * @param type   the new biome type
	 * @param setter the block's setter.
	 * @return true if it has been set, false otherwise.
	 */
	default boolean setBiomeType(Location loc, BiomeType type, Object setter) {
		return setBiomeType(loc.getBlockX(), loc.getBlockZ(), type, setter);
	}
}