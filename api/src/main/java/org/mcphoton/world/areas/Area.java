package org.mcphoton.world.areas;

import org.mcphoton.utils.Location;
import org.mcphoton.world.World;

/**
 * A 3D area that contains blocks.
 *
 * @author TheElectronWill
 */
public interface Area extends Iterable<Location> {
	/**
	 * Checks if the given coordinates are inside this area.
	 */
	boolean contains(int x, int y, int z);

	/**
	 * Checks if the given location is inside this area.
	 */
	default boolean contains(Location loc) {
		return loc.getWorld() == getWorld() && contains(loc.getBlockX(), loc.getBlockY(),
														loc.getBlockZ());
	}

	/**
	 * Gets the area's world.
	 */
	World getWorld();

	/**
	 * Gets the area's size, in blocks (empty blocks are counted).
	 */
	int size();
}