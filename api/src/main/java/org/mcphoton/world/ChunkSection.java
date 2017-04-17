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
package org.mcphoton.world;

import java.io.IOException;
import java.io.OutputStream;
import org.mcphoton.network.ProtocolWriteable;

/**
 * A chunk section: 16x16x16 blocks.
 *
 * @author TheElectronWill
 * @see https://github.com/mcphoton/Photon-API/wiki/IDs-of-blocks,-items-and-entities
 */
public interface ChunkSection extends ProtocolWriteable {

	/**
	 * @param x X coordinate in the section.
	 * @param y Y coordinate in the section.
	 * @param z Z coordinate in the section.
	 * @return the block's simple id (without its metadata).
	 */
	int getBlockId(int x, int y, int z);

	/**
	 * Sets a block's id (simple id, without the metadata).
	 *
	 * @param x X coordinate in the section.
	 * @param y Y coordinate in the section.
	 * @param z Z coordinate in the section.
	 * @param blockId the block's simple id (without its metadata).
	 */
	void setBlockId(int x, int y, int z, int blockId);

	/**
	 * @param x X coordinate in the section.
	 * @param y Y coordinate in the section.
	 * @param z Z coordinate in the section.
	 * @return the block's full id (with its metadata).
	 */
	int getBlockFullId(int x, int y, int z);

	/**
	 * Sets a block's full id (with the metadata).
	 *
	 * @param x X coordinate in the section.
	 * @param y Y coordinate in the section.
	 * @param z Z coordinate in the section.
	 * @param blockFullId the block's full id (with its metadata).
	 */
	void setBlockFullId(int x, int y, int z, int blockFullId);

	/**
	 * @param x X coordinate in the section.
	 * @param y Y coordinate in the section.
	 * @param z Z coordinate in the section.
	 * @return the block's metadata (without its id).
	 */
	int getBlockMetadata(int x, int y, int z);

	/**
	 * Sets a block's metadata (without the id).
	 *
	 * @param x X coordinate in the section.
	 * @param y Y coordinate in the section.
	 * @param z Z coordinate in the section.
	 * @param blockMetadata the block's metadata (without its id).
	 */
	void setBlockMetadata(int x, int y, int z, int blockMetadata);

	/**
	 * Fills a part of this section with the specified block type.
	 *
	 * @param x0 initial x coordinate in the section.
	 * @param y0 initial y coordinate in the section.
	 * @param z0 initial z coordinate in the section.
	 * @param x1 final x coordinate in the section.
	 * @param y1 final y coordinate in the section.
	 * @param z1 final z coordinate in the section.
	 * @param blockFullId the block's simple id (without its metadata).
	 */
	void fillBlockId(int x0, int y0, int z0, int x1, int y1, int z1, int blockId);

	/**
	 * Fills a part of this section with the specified block type.
	 *
	 * @param x0 initial x coordinate in the section.
	 * @param y0 initial y coordinate in the section.
	 * @param z0 initial z coordinate in the section.
	 * @param x1 final x coordinate in the section.
	 * @param y1 final y coordinate in the section.
	 * @param z1 final z coordinate in the section.
	 * @param blockFullId the block's full id (with its metadata).
	 */
	void fillBlockFullId(int x0, int y0, int z0, int x1, int y1, int z1, int blockFullId);

	/**
	 * Replaces every occurence of a block's id.
	 *
	 * @param toReplace the id (without metadata) to replace.
	 * @param replacement the replacement.
	 */
	void replaceBlockId(int toReplace, int replacement);

	/**
	 * Replaces every occurence of a block's full id.
	 *
	 * @param toReplace the full id to replace.
	 * @param replacement the replacement.
	 */
	void replaceBlockFullId(int toReplace, int replacement);

	/**
	 * Writes this ChunkSection to an OutputStream. This is mainly used to save the section in a file.
	 *
	 * @param out the stream to write this section to.
	 */
	void writeTo(OutputStream out) throws IOException;

}
