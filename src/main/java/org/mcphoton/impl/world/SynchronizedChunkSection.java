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
import org.mcphoton.network.ProtocolOutputStream;

/**
 * A synchronized (and so thread-safe) implementation of ChunkSection.
 *
 * @author TheElectronWill
 */
public class SynchronizedChunkSection extends ChunkSectionImpl {

	public SynchronizedChunkSection(int bitsPerBlock) {
		super(bitsPerBlock);
	}

	@Override
	public synchronized int getBlockId(int x, int y, int z) {
		return super.getBlockId(x, y, z);
	}

	@Override
	public synchronized void setBlockId(int x, int y, int z, int blockId) {
		super.setBlockId(x, y, z, blockId);
	}

	@Override
	public synchronized void writeTo(ProtocolOutputStream out) throws IOException {
		super.writeTo(out);
	}

}
