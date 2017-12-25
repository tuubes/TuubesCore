package org.tuubes.world;

import org.tuubes.block.BlockType;
import org.tuubes.world.areas.Area;

/**
 * A chunk column: up to 16 {@link ChunkSection}s aligned vertically, for a total of 16x256x16 =
 * 65536 blocks.
 *
 * @author TheElectronWill
 */
public interface ChunkColumn extends Area {
	@Override
	default int size() {
		return 65536;
	}

	/**
	 * Gets the chunk section at the given index. The first section, at index 0, contains the blocks
	 * from y=0 to y=15, the second section contains the blocks from y=16 to y=31, etc.
	 *
	 * @param index the section's index.
	 * @return the chunk section at the given index.
	 */
	ChunkSection getSection(int index);

	/**
	 * Returns a cubic area contained in this chunk column, and defined by its lower corner P1
	 * (x1,y1,z1) and its upper corner P2(x2,y2,z2), defined in relation to the chunk column.
	 */
	Area subArea(int x1, int y1, int z1, int x2, int y2, int z2);

	/**
	 * Gets the biome type of an XZ column.
	 *
	 * @param x the x coordinate, relative to the chunk column
	 * @param z the z coordinate, relative to the chunk column
	 * @return the biome type
	 */
	BiomeType getBiomeType(int x, int z);

	/**
	 * Sets the biome type of an XZ column.
	 *
	 * @param x         the x coordinate, relative to the chunk column
	 * @param z         the z coordinate, relative to the chunk column
	 * @param biomeType the biome type to set
	 */
	void setBiomeType(int x, int z, BiomeType biomeType);

	/**
	 * @param x the x coordinate, relative to the chunk
	 * @param y the y coordinate, relative to the chunk column
	 * @param z the z coordinate, relative to the chunk column
	 * @return the block type
	 */
	BlockType getBlockType(int x, int y, int z);

	/**
	 * Sets a block's id (simple id, without the metadata).
	 *
	 * @param x    the x coordinate, relative to the chunk column
	 * @param y    the y coordinate, relative to the chunk column
	 * @param z    the z coordinate, relative to the chunk column
	 * @param type the block type to set
	 */
	void setBlockType(int x, int y, int z, BlockType type);
}