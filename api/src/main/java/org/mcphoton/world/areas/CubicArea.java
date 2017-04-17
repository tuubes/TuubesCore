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
package org.mcphoton.world.areas;

import java.util.Iterator;
import org.mcphoton.utils.ImmutableLocation;
import org.mcphoton.utils.Location;
import org.mcphoton.world.World;

/**
 * A cubic area.
 *
 * @author TheElectronWill
 */
public final class CubicArea implements Area {

	private final int x1, y1, z1, x2, y2, z2;
	private final World world;

	/**
	 * Creates a new CubicArea.
	 *
	 * @param x1 the lower x coordinate
	 * @param y1 the lower y coordinate
	 * @param z1 the lower z coordinate
	 * @param x2 the upper x coordinate
	 * @param y2 the upper y coordinate
	 * @param z2 the upper z coordinate
	 * @param world the area's world
	 */
	public CubicArea(int x1, int y1, int z1, int x2, int y2, int z2, World world) {
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
		this.world = world;
	}

	/**
	 * Creates a new CubicArea. The upper/lower coordinates are determined automatically based on the
	 * locations' coordinates, so the parameters don't need to be in a particular order.
	 *
	 * @param corner1 the first corner
	 * @param corner2 the second corner
	 */
	public CubicArea(Location corner1, Location corner2) {
		if (corner1.getWorld() != corner2.getWorld()) {
			throw new IllegalArgumentException("The two corners must be in the same world.");
		}
		this.world = corner1.getWorld();

		if (corner1.getBlockX() > corner2.getBlockX()) {
			this.x1 = corner1.getBlockX();
			this.x2 = corner2.getBlockX();
		} else {
			this.x1 = corner2.getBlockX();
			this.x2 = corner1.getBlockX();
		}

		if (corner1.getBlockY() > corner2.getBlockY()) {
			this.y1 = corner1.getBlockY();
			this.y2 = corner2.getBlockY();
		} else {
			this.y1 = corner2.getBlockY();
			this.y2 = corner1.getBlockY();
		}

		if (corner1.getBlockZ() > corner2.getBlockZ()) {
			this.z1 = corner1.getBlockZ();
			this.z2 = corner2.getBlockZ();
		} else {
			this.z1 = corner2.getBlockZ();
			this.z2 = corner1.getBlockZ();
		}
	}

	@Override
	public boolean contains(int x, int y, int z) {
		return x >= x1 && x <= x2 && y >= y1 && y <= y2;
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public Iterator<Location> iterator() {
		return new CubicAreaIterator();
	}

	@Override
	public int size() {
		return (x2 - x1) * (y2 - y1);
	}

	private class CubicAreaIterator implements Iterator<Location> {

		private int x = x1, y = y1, z = z1;
		private boolean hasNext = true;

		@Override
		public boolean hasNext() {
			return hasNext;
		}

		@Override
		public Location next() {
			Location l = new ImmutableLocation(x, y, z, world);
			x++;
			if (x > x2) {
				x = x1;
				z++;
				if (z > z2) {
					z = z1;
					y++;
					if (y > y2) {
						hasNext = false;
					}
				}
			}
			return l;
		}

	}

}
