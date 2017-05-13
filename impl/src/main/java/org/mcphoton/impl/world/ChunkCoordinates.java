package org.mcphoton.impl.world;

/**
 *
 * @author TheElectronWill
 */
public final class ChunkCoordinates {

	public final int x, z;

	public ChunkCoordinates(int x, int z) {
		this.x = x;
		this.z = z;
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash = 31 * hash + this.x;
		hash = 31 * hash + this.z;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof ChunkCoordinates) {
			ChunkCoordinates coords = (ChunkCoordinates) obj;
			return coords.x == x && coords.z == z;
		}
		return false;
	}

}
