package org.mcphoton.block;

import org.mcphoton.world.Location;

public interface BlockEntity extends Cloneable {
	Location getLocation();

	BlockType getBlockType();

	BlockEntity clone();
}