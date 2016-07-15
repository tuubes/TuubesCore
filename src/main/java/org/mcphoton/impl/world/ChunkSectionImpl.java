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
import net.magik6k.bitbuffer.BitBuffer;
import org.mcphoton.network.ProtocolOutputStream;
import org.mcphoton.world.ChunkSection;

/**
 * A simple implementation of ChunkSection. It is not thread-safe.
 *
 * @author TheElectronWill
 */
public class ChunkSectionImpl implements ChunkSection {

	private final byte[] dataBytes;
	private final BitBuffer data;
	private final int bitsPerBlock;
	private static final byte[] EMPTY_LIGHT_DATA = new byte[4096];//1 byte per block
	//TODO blocklight and skylight
	//TODO block entities

	public ChunkSectionImpl(int bitsPerBlock) {
		int bits = bitsPerBlock * 4096;
		this.dataBytes = new byte[(int) Math.ceil(bits / 8)];
		this.data = BitBuffer.wrap(dataBytes);
		this.bitsPerBlock = bitsPerBlock;
	}

	@Override
	public int getBlockId(int x, int y, int z) {
		data.setPosition((y << 8 | z << 4 | x) * bitsPerBlock);
		return data.getInt(bitsPerBlock);
	}

	@Override
	public void setBlockId(int x, int y, int z, int blockId) {
		data.setPosition((y << 8 | z << 4 | x) * bitsPerBlock);
		data.putInt(blockId, bitsPerBlock);
	}

	public int getBitsPerBlockNumber() {
		return bitsPerBlock;
	}

	@Override
	public void writeTo(ProtocolOutputStream out) throws IOException {
		out.writeByte(bitsPerBlock);
		out.writeVarInt(0);//use the global palette
		out.writeVarInt((int) (data.limit() / 8));
		out.write(dataBytes);
		out.write(EMPTY_LIGHT_DATA);//this writes the skylight data, so it works only in the overworld.
	}

}
