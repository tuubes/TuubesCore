package org.tuubes.world;

import org.tuubes.block.BlockType;

/**
 * A chunk section: 16x16x16 blocks.
 *
 * @author TheElectronWill
 */
public interface ChunkSection {
	/**
	 * @param x X coordinate in the section.
	 * @param y Y coordinate in the section.
	 * @param z Z coordinate in the section.
	 * @return the block type
	 */
	BlockType getBlockType(int x, int y, int z);

	/**
	 * Sets a block's id (simple id, without the metadata).
	 *
	 * @param x    X coordinate in the section.
	 * @param y    Y coordinate in the section.
	 * @param z    Z coordinate in the section.
	 * @param type the block type to set
	 */
	void setBlockType(int x, int y, int z, BlockType type);

	void fill(BlockType blockType);

	void fill(BlockType blockType, int x1, int y1, int z1, int x2, int y2, int z2);

	void replace(BlockType type, BlockType replacement);

	void replace(BlockType type, BlockType replacement, int x1, int y1, int z1, int x2, int y2,
				 int z2);
}