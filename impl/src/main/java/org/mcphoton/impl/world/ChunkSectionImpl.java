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
import net.magik6k.bitbuffer.BitBuffer;
import org.mcphoton.world.ChunkSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic implementation of ChunkSection. It is thread-safe.
 *
 * @author TheElectronWill
 */
public final class ChunkSectionImpl implements ChunkSection {

	private static final Logger log = LoggerFactory.getLogger(ChunkSectionImpl.class);
	private final byte[] dataBytes;
	private final BitBuffer data;
	private final int bitsPerBlock;
	private static final byte[] EMPTY_LIGHT_DATA = new byte[4096];//1 byte per block
	//TODO blocklight and skylight
	//TODO block entities

	public ChunkSectionImpl() {
		this(13);
	}

	public ChunkSectionImpl(int bitsPerBlock) {
		int bits = bitsPerBlock * 4096;
		this.dataBytes = new byte[(int)Math.ceil(bits / 8)];
		this.data = BitBuffer.wrap(dataBytes);
		this.bitsPerBlock = bitsPerBlock;
	}

	public ChunkSectionImpl(int bitsPerBlock, byte[] dataBytes) {
		this.dataBytes = dataBytes;
		this.data = BitBuffer.wrap(dataBytes);
		this.bitsPerBlock = bitsPerBlock;
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
		data.setPosition((y << 8 | z << 4 | x) * bitsPerBlock);
		return data.getInt(bitsPerBlock);
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
		data.setPosition((y << 8 | z << 4 | x) * bitsPerBlock);
		data.putInt(blockFullId, bitsPerBlock);
	}

	@Override
	public void setBlockId(int x, int y, int z, int blockId) {
		setBlockFullId(x, y, z, blockId << 4);
	}

	public int getBitsPerBlockNumber() {
		return bitsPerBlock;
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
		out.write(bitsPerBlock);
		out.write(dataBytes);
	}
}