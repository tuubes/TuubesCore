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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import org.mcphoton.world.ChunkColumn;
import org.mcphoton.world.ChunkSection;
import org.mcphoton.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author TheElectronWill
 */
public final class WorldChunksManager {

	private static final Logger log = LoggerFactory.getLogger(WorldChunksManager.class);
	private final World world;
	private final File chunksDirectory;
			
	public WorldChunksManager(World world) {
		this.world = world;
		this.chunksDirectory = new File(world.getDirectory(), "chunks");
	}

	private final Map<ChunkCoordinates, SoftReference<ChunkColumn>> chunks = new HashMap<>(512);
	//TODO a ReferenceQueue that removes the chunk from the map when its reference is collected.
	//TODO save the chunk, if it has been modified, before it gets garbage-collected!

	public ChunkColumn getChunk(int cx, int cz, boolean createIfNeeded) {
		ChunkCoordinates coords = new ChunkCoordinates(cx, cz);
		SoftReference<ChunkColumn> ref = chunks.get(coords);
		ChunkColumn chunk;
		if (ref == null || (chunk = ref.get()) == null) {//chunk not loaded
			File chunkFile = getChunkFile(cx, cz);
			if (chunkFile.exists()) {//the chunk exists on the disk -> read it
				chunk = tryReadChunk(chunkFile);
				chunks.put(coords, new SoftReference<>(chunk));
			} else {//the chunk doesn't exist at all -> generate it
				chunk = generateChunk(cx, cz);
				chunks.put(coords, new SoftReference<>(chunk));
			}
		}
		return chunk;
	}

	ChunkColumn readChunk(File chunkFile) throws IOException {
		try (FileInputStream in = new FileInputStream(chunkFile)) {
			byte[] biomes = new byte[256];
			in.read(biomes);

			int sectionsArraySize = in.read();
			ChunkSection[] sections = new ChunkSection[sectionsArraySize];

			for (int i = 0; i < sectionsArraySize; i++) {
				int bitsPerBlock = in.read();
				byte[] data = new byte[(int) Math.ceil(bitsPerBlock * 4096 / 8)];
				in.read(data);
				sections[i] = new ChunkSectionImpl(bitsPerBlock, data);
			}

			return new ChunkColumnImpl(biomes, sections);
		}
	}

	ChunkColumn tryReadChunk(File chunkFile) {
		try {
			return readChunk(chunkFile);
		} catch (IOException ex) {
			log.error("Unable to read chunk from {}", chunkFile, ex);
			return null;
		}
	}

	ChunkColumn generateChunk(int cx, int cz) {
		return world.getChunkGenerator().generate(cx, cz);
	}

	void writeChunk(int cx, int cz, ChunkColumn chunk) throws IOException {
		File chunkFile = getChunkFile(cx, cz);
		try (FileOutputStream out = new FileOutputStream(chunkFile)) {
			chunk.writeTo(out);
		}
	}

	void writeAll() {
		if (!chunksDirectory.exists()) {
			chunksDirectory.mkdirs();
		}
		for (Map.Entry<ChunkCoordinates, SoftReference<ChunkColumn>> entry : chunks.entrySet()) {
			ChunkCoordinates coords = entry.getKey();
			SoftReference<ChunkColumn> ref = entry.getValue();
			ChunkColumn chunk = ref.get();
			if (chunk != null) {
				File chunkFile = getChunkFile(coords.x, coords.z);
				try (FileOutputStream out = new FileOutputStream(chunkFile)) {
					chunk.writeTo(out);
				} catch (IOException ex) {
					log.error("Unable to write the chunk to {}", chunkFile.toString(), ex);
				}
			}
		}
	}

	File getChunkFile(int cx, int cz) {
		return new File(chunksDirectory, cx + "_" + cz + ".chunk");
	}

}
