/*
 * Copyright (c) 2016 MCPhoton <http://mcphoton.org> and contributors.
 *
 * This file is part of the Photon Server Implementation <https://github.com/mcphoton/Photon-Server>.
 *
 * The Photon Server Implementation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Photon Server Implementation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mcphoton.impl.world;

import java.io.IOException;
import java.io.OutputStream;
import org.mcphoton.world.ChunkColumn;
import org.mcphoton.world.ChunkSection;

/**
 * Basic implementation of ChunkColumn. It is thread-safe.
 *
 * @see http://wiki.vg/SMP_Map_Format
 * @author TheElectronWill
 */
public final class ChunkColumnImpl implements ChunkColumn {

	private final byte[] biomes;
	private final ChunkSection[] sections;
	
	//Temp:
	public byte[] getBiomes() {
		return biomes;
	}

	public ChunkSection[] getSections() {
		return sections;
	}
	

	public ChunkColumnImpl(byte[] biomes, ChunkSection[] sections) {
		this.biomes = biomes;
		this.sections = sections;
	}

	public ChunkColumnImpl() {
		this.biomes = new byte[256];
		this.sections = new ChunkSection[16];
	}

	@Override
	public int getBiomeId(int x, int z) {
		synchronized (biomes) {
			return biomes[z * 16 + x];
		}
	}

	@Override
	public synchronized void setBiomeId(int x, int z, int biomeId) {
		synchronized (biomes) {
			biomes[z * 16 + x] = (byte) biomeId;
		}
	}

	@Override
	public int getBlockFullId(int x, int y, int z) {
		int sectionIndex = y / 16;
		int yInSection = y & 15;// Fast remainder: if divisor is a power of two, value & (divisor - 1) is equal to value % divisor
		synchronized (sections) {
			return sections[sectionIndex].getBlockFullId(x, yInSection, z);
		}
	}

	@Override
	public void setBlockFullId(int x, int y, int z, int blockFullId) {
		int sectionIndex = y / 16;
		int yInSection = y & 15;// Fast remainder: if divisor is a power of two, value & (divisor - 1) is equal to value % divisor
		synchronized (sections) {
			sections[sectionIndex].setBlockFullId(x, yInSection, z, blockFullId);
		}
	}

	@Override
	public int getBlockId(int x, int y, int z) {
		int sectionIndex = y / 16;
		int yInSection = y & 15;// Fast remainder: if divisor is a power of two, value & (divisor - 1) is equal to value % divisor
		synchronized (sections) {
			return sections[sectionIndex].getBlockId(x, yInSection, z);
		}
	}

	@Override
	public void setBlockId(int x, int y, int z, int blockId) {
		int sectionIndex = y / 16;
		int yInSection = y & 15;// Fast remainder: if divisor is a power of two, value & (divisor - 1) is equal to value % divisor
		synchronized (sections) {
			sections[sectionIndex].setBlockId(x, yInSection, z, blockId);
		}
	}

	@Override
	public int getBlockMetadata(int x, int y, int z) {
		int sectionIndex = y / 16;
		int yInSection = y & 15;// Fast remainder: if divisor is a power of two, value & (divisor - 1) is equal to value % divisor
		synchronized (sections) {
			return sections[sectionIndex].getBlockMetadata(x, yInSection, z);
		}
	}

	@Override
	public void setBlockMetadata(int x, int y, int z, int blockMetadata) {
		int sectionIndex = y / 16;
		int yInSection = y & 15;// Fast remainder: if divisor is a power of two, value & (divisor - 1) is equal to value % divisor
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
		sections[index] = section;
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
		out.write(biomes);
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
