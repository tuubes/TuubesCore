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
package org.mcphoton.block;

import org.mcphoton.utils.Location;
import org.mcphoton.utils.Vector;

/**
 * A block face.
 *
 * @author TheElectronWill
 */
public enum BlockFace {
	/**
	 * Special BlockFace with modX = modY = modZ = 0.
	 */
	SELF(0, 0, 0) {
		public BlockFace getOppositeFace() {
			return SELF;
		}
	}, NORTH(0, 0, -1) {
		public BlockFace getOppositeFace() {
			return SOUTH;
		}
	}, SOUTH(0, 0, 1) {
		public BlockFace getOppositeFace() {
			return NORTH;
		}
	}, EAST(1, 0, 0) {
		public BlockFace getOppositeFace() {
			return WEST;
		}
	}, WEST(-1, 0, 0) {
		public BlockFace getOppositeFace() {
			return EAST;
		}
	}, UP(0, 1, 0) {
		public BlockFace getOppositeFace() {
			return DOWN;
		}
	}, DOWN(0, -1, 0) {
		public BlockFace getOppositeFace() {
			return UP;
		}
	}, NORTH_EAST(1, 0, -1) {
		public BlockFace getOppositeFace() {
			return SOUTH_WEST;
		}
	}, NORTH_WEST(-1, 0, -1) {
		public BlockFace getOppositeFace() {
			return SOUTH_EAST;
		}
	}, SOUTH_EAST(1, 0, 1) {
		public BlockFace getOppositeFace() {
			return NORTH_WEST;
		}
	}, SOUTH_WEST(-1, 0, 1) {
		public BlockFace getOppositeFace() {
			return NORTH_EAST;
		}
	};

	private final int modX, modY, modZ;

	BlockFace(int modX, int modY, int modZ) {
		this.modX = modX;
		this.modY = modY;
		this.modZ = modZ;
	}

	/**
	 * @return the modification to apply on the x coordinate to get the corresponding block.
	 */
	public int getModX() {
		return modX;
	}

	/**
	 * @return the modification to apply on the y coordinate to get the corresponding block.
	 */
	public int getModY() {
		return modY;
	}

	/**
	 * @return the modification to apply on the z coordinate to get the corresponding block.
	 */
	public int getModZ() {
		return modZ;
	}

	/**
	 * @return the opposite BlockFace.
	 */
	public abstract BlockFace getOppositeFace();

	/**
	 * Adds modX, modY and modZ to a Location.
	 *
	 * @param l the location
	 * @return a new location that is the result of the modification.
	 */
	public Location applyMod(Location l) {
		return l.add(modX, modY, modZ);
	}

	/**
	 * Adds modX, modY and modZ to a Vector.
	 *
	 * @param v the vector to modify
	 * @return the vector v
	 */
	public Vector applyMod(Vector v) {
		return v.add(toVector());
	}

	/**
	 * @return a new Vector (modX, modY, modZ).
	 */
	public Vector toVector() {
		return new Vector(modX, modY, modZ);
	}
}