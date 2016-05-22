/*
 * Copyright (c) 2016 MCPhoton <http://mcphoton.org> and contributors.
 *
 * This file is part of the Photon Server Implementation <https://github.com/mcphoton/Photon-Server>.
 *
 * The Photon Server Implementation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Photon Server Implementation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mcphoton.impl.network;

import java.nio.ByteBuffer;

/**
 * A codec is a component that encodes and decodes data. It takes a ByteBuffer as input, works with it and
 * returns the result as a ByteBuffer.
 *
 * @author TheElectronWill
 *
 */
public interface Codec {

	/**
	 * Encodes some data. The returned ByteBuffer may be a new one, or the <code>data</code> one. Its position
	 * will be 0 and its limit will be correctly set.
	 *
	 * @param data a ByteBuffer that contains the data to encode. Its position should be 0.
	 * @return a ByteBuffer that contains the encoded data
	 */
	ByteBuffer encode(ByteBuffer data) throws Exception;

	/**
	 * Decodes some data. The returned ByteBuffer may be a new one, or the <code>data</code> one. Its position
	 * will be 0 and its limit will be correctly set.
	 *
	 * @param data a ByteBuffer that contains the data to decode. Its position should be 0.
	 * @return a ByteBuffer that contains the decoded data
	 */
	ByteBuffer decode(ByteBuffer data) throws Exception;

}
