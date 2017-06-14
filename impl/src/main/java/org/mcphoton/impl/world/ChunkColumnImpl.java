package org.mcphoton.impl.world;

import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import java.io.IOException;
import java.io.OutputStream;
import org.mcphoton.world.ChunkColumn;
import org.mcphoton.world.ChunkSection;

/**
 * Basic implementation of ChunkColumn. It is thread-safe.
 *
 * @author TheElectronWill
 * @see <a href="http://wiki.vg/SMP_Map_Format">wiki.vg - Protocol map format</a>
 */
public final class ChunkColumnImpl implements ChunkColumn {
	private final Column libColumn;
	private final ChunkSectionImpl[] sections;

	public ChunkColumnImpl(int x, int z, ChunkSectionImpl[] sections, byte[] biomes,
						   CompoundTag[] tileEntities) {
		this.sections = sections;
		Chunk[] chunks = new Chunk[sections.length];
		for (int i = 0; i < sections.length; i++) {
			ChunkSectionImpl section = sections[i];
			chunks[i] = section.libChunk;
		}
		this.libColumn = new Column(x, z, chunks, biomes, tileEntities);
	}

	@Override
	public int getBiomeId(int x, int z) {
		byte[] biomes = libColumn.getBiomeData();
		synchronized (biomes) {
			return biomes[z * 16 + x];
		}
	}

	@Override
	public synchronized void setBiomeId(int x, int z, int biomeId) {
		byte[] biomes = libColumn.getBiomeData();
		synchronized (biomes) {
			biomes[z * 16 + x] = (byte)biomeId;
		}
	}

	@Override
	public int getBlockFullId(int x, int y, int z) {
		int sectionIndex = y / 16;
		int yInSection = y & 15;
		// Fast remainder: if divisor is a power of two, value & (divisor - 1) is equal to value % divisor
		synchronized (sections) {
			return sections[sectionIndex].getBlockFullId(x, yInSection, z);
		}
	}

	@Override
	public void setBlockFullId(int x, int y, int z, int blockFullId) {
		int sectionIndex = y / 16;
		int yInSection = y & 15;// Fast remainder
		synchronized (sections) {
			sections[sectionIndex].setBlockFullId(x, yInSection, z, blockFullId);
		}
	}

	@Override
	public int getBlockId(int x, int y, int z) {
		int sectionIndex = y / 16;
		int yInSection = y & 15;// Fast remainder
		synchronized (sections) {
			return sections[sectionIndex].getBlockId(x, yInSection, z);
		}
	}

	@Override
	public void setBlockId(int x, int y, int z, int blockId) {
		int sectionIndex = y / 16;
		int yInSection = y & 15;// Fast remainder
		synchronized (sections) {
			sections[sectionIndex].setBlockId(x, yInSection, z, blockId);
		}
	}

	@Override
	public int getBlockMetadata(int x, int y, int z) {
		int sectionIndex = y / 16;
		int yInSection = y & 15;// Fast remainder
		synchronized (sections) {
			return sections[sectionIndex].getBlockMetadata(x, yInSection, z);
		}
	}

	@Override
	public void setBlockMetadata(int x, int y, int z, int blockMetadata) {
		int sectionIndex = y / 16;
		int yInSection = y & 15;// Fast remainder
		synchronized (sections) {
			sections[sectionIndex].setBlockMetadata(x, yInSection, z, blockMetadata);
		}
	}

	@Override
	public ChunkSection getSection(int index) {
		return sections[index];
	}

	@Override
	public void setSection(int index, ChunkSection section) {
		ChunkSectionImpl impl = (ChunkSectionImpl)section;
		sections[index] = impl;
		libColumn.getChunks()[index] = impl.libChunk;
	}

	@Override
	public void fillBlockFullId(int x0, int y0, int z0, int x1, int y1, int z1, int blockFullId) {
		for (int y = y0; y < y1; y++) {
			int sectionIndex = y / 16;
			int yInSection = y & 15;
			ChunkSection section;
			synchronized (sections) {
				section = sections[sectionIndex];
			}
			section.fillBlockFullId(x0, yInSection, z0, x1, yInSection + 1, z1, blockFullId);
		}
	}

	@Override
	public void fillBlockId(int x0, int y0, int z0, int x1, int y1, int z1, int blockId) {
		for (int y = y0; y < y1; y++) {
			int sectionIndex = y / 16;
			int yInSection = y & 15;
			ChunkSection section;
			synchronized (sections) {
				section = sections[sectionIndex];
			}
			section.fillBlockId(x0, yInSection, z0, x1, yInSection + 1, z1, blockId);
		}
	}

	@Override
	public void replaceBlockFullId(int toReplace, int replacement) {
		for (int i = 0; i < 16; i++) {
			ChunkSection section;
			synchronized (sections) {
				section = sections[i];
			}
			section.replaceBlockFullId(toReplace, replacement);
		}
	}

	@Override
	public void replaceBlockId(int toReplace, int replacement) {
		for (int i = 0; i < 16; i++) {
			ChunkSection section;
			synchronized (sections) {
				section = sections[i];
			}
			section.replaceBlockId(toReplace, replacement);
		}
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		out.write(sections.length);
		for (ChunkSection section : sections) {
			if (section == null) {
				out.write(0);
			} else {
				section.writeTo(out);
			}
		}
	}
}