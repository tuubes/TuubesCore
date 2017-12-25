package org.tuubes.block;

import com.electronwill.utils.Vec3d;

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
	 * @return a new Vector (modX, modY, modZ).
	 */
	public Vec3d toVector() {
		return new Vec3d(modX, modY, modZ);
	}
}