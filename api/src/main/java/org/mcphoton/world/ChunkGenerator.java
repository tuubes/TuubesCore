package org.mcphoton.world;

/**
 * @author TheElectronWill
 */
public interface ChunkGenerator {
	/**
	 * Generates a chunk.
	 *
	 * @param cx the chunk's x coordinate.
	 * @param cz the chunk's z coordinate.
	 * @return the generated ChunkColumn.
	 */
	ChunkColumn generate(int cx, int cz);
}