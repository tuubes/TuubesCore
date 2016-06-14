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
 * A codec that does nothing.
 *
 * @author TheElectronWill
 */
public final class NoCodec implements Codec {

	/**
	 * Does nothing.
	 *
	 * @return the data buffer, as it is.
	 */
	@Override
	public ByteBuffer encode(ByteBuffer data) {
		return data;
	}

	/**
	 * Does nothing.
	 *
	 * @return the data buffer, as it is.
	 */
	@Override
	public ByteBuffer decode(ByteBuffer data) {
		return data;
	}

}
