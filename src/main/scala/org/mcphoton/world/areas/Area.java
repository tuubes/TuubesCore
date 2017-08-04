package org.mcphoton.world.areas;

import org.mcphoton.block.BlockType;
import org.mcphoton.world.Location;
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
		return loc.getWorld() == getWorld() && contains(loc.getBlockX(), loc.getBlockY(),
														loc.getBlockZ());
	}

	/**
	 * Gets the area's world.
	 */
	World getWorld();

	/**
	 * Gets the area's size, in blocks (empty blocks are counted).
	 */
	int size();

	/**
	 * Fills the area with a given block.
	 *
	 * @param blockType the block type
	 */
	void fill(BlockType blockType);

	/**
	 * Replaces all occurences of a block by another one.
	 *
	 * @param type        the type to replace
	 * @param replacement the replacement type
	 */
	void replace(BlockType type, BlockType replacement);
}