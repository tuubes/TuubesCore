package org.tuubes.world;

import com.github.steveice10.mc.protocol.data.game.chunk.BlockStorage;
import com.github.steveice10.mc.protocol.data.game.chunk.ChunkSectionData;
import com.github.steveice10.mc.protocol.data.game.chunk.NibbleArray3d;
import org.tuubes.GameRegistry;
import org.tuubes.block.BlockType;

/**
 * Basic implementation of ChunkSection. It is thread-safe.
 *
 * @author TheElectronWill
 */
public final class ChunkSectionImpl implements ChunkSection, ChunkSectionData {
	/**
	 * true iff the section has changed since the last save.
	 */
	private volatile boolean changed;
	/**
	 * Contains the blocks ids and palette.
	 */
	private final BlockStorage blocks;
	private final NibbleArray3d blockLight, skyLight;

	ChunkSectionImpl(BlockStorage blocks, NibbleArray3d blockLight, NibbleArray3d skyLight) {
		this.blocks = blocks;
		this.blockLight = blockLight;
		this.skyLight = skyLight;
	}

	public ChunkSectionImpl(boolean hasSkylight) {
		this.blocks = new BlockStorage();
		this.blockLight = new NibbleArray3d(4096);
		this.skyLight = new NibbleArray3d(4096);
	}

	public boolean hasChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	@Override
	public BlockStorage getBlocks() {
		return blocks;
	}

	@Override
	public NibbleArray3d getBlockLight() {
		return blockLight;
	}

	@Override
	public NibbleArray3d getSkyLight() {
		return skyLight;
	}

	@Override
	public BlockType getBlockType(int x, int y, int z) {
		int typeId = blocks.get(x, y, z);
		return GameRegistry.block$$temp(typeId);
	}

	@Override
	public void setBlockType(int x, int y, int z, BlockType type) {
		changed = true;
		blocks.set(x, y, z, type.fullId());
	}

	@Override
	public void fill(BlockType blockType) {
		//TODO
	}

	@Override
	public void fill(BlockType blockType, int x1, int y1, int z1, int x2, int y2, int z2) {
		//TODO
	}

	@Override
	public void replace(BlockType type, BlockType replacement) {
		//TODO
	}

	@Override
	public void replace(BlockType type, BlockType replacement, int x1, int y1, int z1, int x2,
						int y2, int z2) {
		//TODO
	}
	//TODO method sendUpdates()
}