package org.tuubes.block;

import org.tuubes.world.Location;

public interface BlockEntity extends Cloneable {
	Location getLocation();

	BlockType getBlockType();

	BlockEntity clone();
}