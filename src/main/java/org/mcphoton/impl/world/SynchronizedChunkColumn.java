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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.mcphoton.world.ChunkColumn;
import org.mcphoton.world.ChunkSection;
import org.mcphoton.world.World;

/**
 * A thread-safe implementation of ChunkColumn.
 *
 * @author TheElectronWill
 */
public class SynchronizedChunkColumn implements ChunkColumn {

	private final int x, z;
	private final World world;
	private final byte[] biomesData;
	private final ChunkSection[] sections;
	private final Collection<ChunkSection> unmodifiableCollection;

	public SynchronizedChunkColumn(int x, int z, World world) {
		this(x, z, world, new ChunkSection[16], new byte[256]);
	}

	public SynchronizedChunkColumn(int x, int z, World world, ChunkSection[] sections, byte[] biomesData) {
		this.x = x;
		this.z = z;
		this.world = world;
		this.sections = sections;
		unmodifiableCollection = Collections.unmodifiableList(Arrays.asList(sections));
		this.biomesData = biomesData;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getZ() {
		return z;
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public Collection<ChunkSection> getSections() {
		return unmodifiableCollection;
	}

	@Override
	public synchronized int getBiomeId(int x, int z) {
		return biomesData[z << 4 | x];
	}

	@Override
	public synchronized void setBiomeId(int x, int z, int biomeId) {
		biomesData[z << 4 | x] = (byte) biomeId;
	}

}
