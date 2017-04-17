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

import java.util.Optional;
import org.mcphoton.world.ChunkColumn;
import org.mcphoton.world.World;
import org.mcphoton.world.areas.Area;
import org.mcphoton.world.protection.CheckedWorldAccess;
import org.mcphoton.world.protection.UnlockedAreaAccess;
import org.mcphoton.world.protection.UnlockedWorldAccess;
import org.mcphoton.world.protection.WorldAccessManager;

/**
 *
 * @author TheElectronWill
 */
public final class OpenWorldAccessManager implements WorldAccessManager {

	private final World world;

	public OpenWorldAccessManager(World world) {
		this.world = world;
	}

	@Override
	public CheckedWorldAccess getAccess() {
		throw new UnsupportedOperationException();//TODO
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public Optional<UnlockedAreaAccess> unlockArea(Area area, Object accessor) {
		throw new UnsupportedOperationException();//TODO
	}

	@Override
	public Optional<ChunkColumn> unlockChunk(int x, int z, Object accessor) {
		throw new UnsupportedOperationException();//TODO
	}

	@Override
	public Optional<UnlockedWorldAccess> unlockWorld(Object accessor) {
		throw new UnsupportedOperationException();//TODO
	}

}
