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
	 * Gets a CheckedWorldAccess for the world. The WorldAccessManager's implementation may return
	 * the same CheckedWorldAccess for different invocations of this method.
	 *
	 * @return a CheckedWorldAccess to the world.
	 */
	CheckedWorldAccess getAccess();

	/**
	 * Tries to unlock an area. The WorldAccessManager's implementation may return the same
	 * object for different invocations of this method.
	 *
	 * @param area     the area to unlock.
	 * @param accessor the object that would like to unlock the area.
	 * @return an UnlockedAreaAccess, which doesn't check if you have the permission to modify the
	 * area.
	 */
	Optional<UnlockedAreaAccess> unlockArea(Area area, Object accessor);

	/**
	 * Tries to unlock a chunk column. The WorldAccessManager's implementation may return the same
	 * object for different invocations of this method.
	 *
	 * @param x        the chunk's x coordinate.
	 * @param z        the chunk's z coordinate.
	 * @param accessor the object that would like to unlock the chunk.
	 * @return a ChunkColumn, which doesn't check if you have the permission to modify the chunk.
	 */
	Optional<ChunkColumn> unlockChunk(int x, int z, Object accessor);

	/**
	 * Tries to unlock the entire world. The WorldAccessManager's implementation may return the same
	 * object for different invocations of this method.
	 *
	 * @param accessor the object that would like to unlock the area.
	 * @return an UnlockedWorldAccess, which doesn't check if you have the permission to modify the
	 * world.
	 */
	Optional<UnlockedWorldAccess> unlockWorld(Object accessor);
}