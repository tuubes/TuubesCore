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

import java.util.Objects;
import org.mcphoton.Photon;

/**
 * Represents the data that defines a block: its type and its metadata.
 *
 * @see https://github.com/mcphoton/Photon-API/wiki/IDs-of-blocks,-items-and-entities
 * @author TheElectronWill
 */
public final class BlockData implements Cloneable {

	private final BlockType type;
	private final byte metadata;

	/**
	 * Creates a new BlockData.
	 *
	 * @param type the block's type.
	 * @param metadata the block's meta data, between 0 and 15 (inclusive).
	 */
	public BlockData(BlockType type, byte metadata) {
		this.type = type;
		this.metadata = metadata;
	}

	/**
	 * Creates a new BlockData with a metadata of 0.
	 *
	 * @param type the block's type.
	 */
	public BlockData(BlockType type) {
		this.type = type;
		this.metadata = 0;
	}

	/**
	 * Creates a new BlockData with a full block id.
	 *
	 * @param fullId the block's full id, ie its type id and its metadata.
	 * @see #getFullId()
	 */
	public BlockData(int fullId) {
		this.type = Photon.getGameRegistry().getRegisteredBlock(fullId >> 4);
		this.metadata = (byte) (fullId & 15);
	}

	/**
	 * @return the block's type.
	 */
	public BlockType getType() {
		return type;
	}

	/**
	 * @return the block's type id, without the metadata.
	 */
	public int getTypeId() {
		return type.getId();
	}

	/**
	 * @return the block's metadata, between 0 and 15 (inclusive).
	 */
	public byte getMetadata() {
		return metadata;
	}

	/**
	 * Calculates and returns the "full" block id: its type id + its metadata, constructed like this:
	 * {@code int fullId = type.getId() << 4 | (meta & 15)}
	 *
	 * @return the block's full id.
	 */
	public int getFullId() {
		return type.getId() << 4 | (metadata & 15);
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 89 * hash + Objects.hashCode(this.type);
		hash = 89 * hash + this.metadata;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof BlockData) {
			BlockData other = (BlockData) obj;
			return metadata == other.metadata && type.equals(other.type);
		}
		return false;
	}

	@Override
	public BlockData clone() {
		return new BlockData(type, metadata);
	}

}
