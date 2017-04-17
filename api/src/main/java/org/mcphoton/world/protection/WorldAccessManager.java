/*
 * Copyright (c) 2016 MCPhoton <http://mcphoton.org> and contributors.
 *
 * This file is part of the Photon API <https://github.com/mcphoton/Photon-API>.
 *
 * The Photon API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Photon API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mcphoton.world.protection;

import java.util.Optional;
import org.mcphoton.world.ChunkColumn;
import org.mcphoton.world.World;
import org.mcphoton.world.areas.Area;

/**
 * Manages accesses.
 *
 * @author TheElectronWill
 */
public interface WorldAccessManager {

	/**
	 * @return the world this access manager works in.
	 */
	World getWorld();

	/**
	 * Gets a CheckedWorldAccess for the world. The WorldAccessManager's implementation may return the same
	 * CheckedWorldAccess for different invocations of this method.
	 *
	 * @return a CheckedWorldAccess to the world.
	 */
	CheckedWorldAccess getAccess();

	/**
	 * Tries to unlock an area. The WorldAccessManager's implementation may return the same
	 * object for different invocations of this method.
	 *
	 * @param area the area to unlock.
	 * @param accessor the object that would like to unlock the area.
	 * @return an UnlockedAreaAccess, which doesn't check if you have the permission to modify the area.
	 */
	Optional<UnlockedAreaAccess> unlockArea(Area area, Object accessor);

	/**
	 * Tries to unlock a chunk column. The WorldAccessManager's implementation may return the same
	 * object for different invocations of this method.
	 *
	 * @param x the chunk's x coordinate.
	 * @param z the chunk's z coordinate.
	 * @param accessor the object that would like to unlock the chunk.
	 * @return a ChunkColumn, which doesn't check if you have the permission to modify the chunk.
	 */
	Optional<ChunkColumn> unlockChunk(int x, int z, Object accessor);

	/**
	 * Tries to unlock the entire world. The WorldAccessManager's implementation may return the same
	 * object for different invocations of this method.
	 *
	 * @param accessor the object that would like to unlock the area.
	 * @return an UnlockedWorldAccess, which doesn't check if you have the permission to modify the world.
	 */
	Optional<UnlockedWorldAccess> unlockWorld(Object accessor);

}
