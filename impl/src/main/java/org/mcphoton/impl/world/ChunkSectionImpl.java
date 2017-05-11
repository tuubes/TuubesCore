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

import com.github.steveice10.mc.protocol.data.game.chunk.Chunk;
import com.github.steveice10.mc.protocol.data.game.chunk.FlexibleStorage;
import com.github.steveice10.mc.protocol.data.game.world.block.BlockState;
import java.io.IOException;
import java.io.OutputStream;
import org.mcphoton.world.ChunkSection;

/**
 * Basic implementation of ChunkSection. It is thread-safe.
 *
 * @author TheElectronWill
 */
public final class ChunkSectionImpl implements ChunkSection {
	final Chunk libChunk;

	public ChunkSectionImpl(boolean skylight) {
		this.libChunk = new Chunk(skylight);
	}

	@Override
	public synchronized void fillBlockFullId(int x0, int y0, int z0, int x1, int y1, int z1,
											 int blockFullId) {
		for (int x = x0; x < x1; x++) {
			for (int y = y0; y < y1; y++) {
				for (int z = z0; z < z1; z++) {
					setBlockFullId(x, y, z, blockFullId);
				}
			}
		}
	}

	@Override
	public synchronized void fillBlockId(int x0, int y0, int z0, int x1, int y1, int z1,
										 int blockId) {
		for (int x = x0; x < x1; x++) {
			for (int y = y0; y < y1; y++) {
				for (int z = z0; z < z1; z++) {
					setBlockId(x, y, z, blockId);
				}
			}
		}
	}

	@Override
	public synchronized int getBlockFullId(int x, int y, int z) {
		BlockState libBlockState = libChunk.getBlocks().get(x, y, z);
		return libBlockState.getId() << 4 | libBlockState.getData();
	}

	@Override
	public int getBlockId(int x, int y, int z) {
		return getBlockFullId(x, y, z) >> 4;
	}

	@Override
	public int getBlockMetadata(int x, int y, int z) {
		return getBlockFullId(x, y, z) & 15;
	}

	@Override
	public synchronized void replaceBlockFullId(int toReplace, int replacement) {
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					int fullId = getBlockFullId(x, y, z);
					if (fullId == toReplace) {
						setBlockFullId(x, y, z, replacement);
					}
				}
			}
		}
	}

	@Override
	public synchronized void replaceBlockId(int toReplace, int replacement) {
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					int id = getBlockId(x, y, z);
					if (id == toReplace) {
						setBlockId(x, y, z, replacement);
					}
				}
			}
		}
	}

	@Override
	public synchronized void setBlockFullId(int x, int y, int z, int blockFullId) {
		BlockState libBlockState = new BlockState(blockFullId >> 4, blockFullId & 0xf);
		libChunk.getBlocks().set(x, y, z, libBlockState);
	}

	@Override
	public void setBlockId(int x, int y, int z, int blockId) {
		setBlockFullId(x, y, z, blockId << 4);
	}

	@Override
	public synchronized void setBlockMetadata(int x, int y, int z, int blockMetadata) {
		int fullId = getBlockFullId(x, y, z);
		fullId &= ~15;//set metadata to zero
		fullId |= (blockMetadata & 15);//set metadata to the specified value
		setBlockFullId(x, y, z, fullId);
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		FlexibleStorage storage = libChunk.getBlocks().getStorage();
		out.write(storage.getBitsPerEntry());
		out.write(storage.getData().length);
		for (long l : storage.getData()) {
			out.write((int)(l >>> 8));
			out.write((int)l);
		}
	}
}