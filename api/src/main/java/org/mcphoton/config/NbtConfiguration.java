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
package org.mcphoton.config;

import org.mcphoton.network.NetOutput;
import org.mcphoton.network.NetWriteable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * A NBT configuration.
 *
 * @see http://wiki.vg/NBT
 * @author TheElectronWill
 */
public class NbtConfiguration extends BaseConfiguration implements NetWriteable {

	public NbtConfiguration() {
		// defined by the photon's implementation
	}

	public NbtConfiguration(Map<String, Object> data) {
		// defined by the photon's implementation
	}

	public NbtConfiguration(Map<String, Object> data, String name) {
		// defined by the photon's implementation
	}

	public NbtConfiguration(File file) throws IOException {
		// defined by the photon's implementation
	}

	public NbtConfiguration(InputStream in) throws IOException {
		// defined by the photon's implementation
	}

	public NbtConfiguration(ByteBuffer in) throws IOException {
		// defined by the photon's implementation
	}

	public String getName() {
		return null;
		//defined by the photon's implementation
	}

	public boolean containsByteArray(String key) {
		return false;
		// defined by the photon's implementation
	}

	public boolean containsIntArray(String key) {
		return false;
		// defined by the photon's implementation
	}

	public byte[] getByteArray(String key) {
		return null;
		// defined by the photon's implementation
	}

	public int[] getIntArray(String key) {
		return null;
		// defined by the photon's implementation
	}

	@Override
	public void readFrom(InputStream in) throws IOException {
		// defined by the photon's implementation
	}

	public void readFrom(ByteBuffer buff) throws IOException {
		// defined by the photon's implementation
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		// defined by the photon's implementation
	}

	@Override
	public void writeTo(NetOutput out) throws IOException {
		//defined by the photon's implementation
	}

}
