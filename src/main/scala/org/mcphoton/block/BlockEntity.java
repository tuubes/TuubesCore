package org.mcphoton.block;

import org.mcphoton.utils.Location;

public interface BlockEntity extends Cloneable {
	Location getLocation();

	BlockType getBlockType();

	BlockEntity clone();
}