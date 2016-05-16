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
package org.mcphoton.config;

import com.electronwill.nbt.Nbt;
import com.electronwill.nbt.NbtReader;
import com.electronwill.nbt.ReadTagCompound;
import com.electronwill.utils.DataInputBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import org.mcphoton.network.ProtocolOutputStream;
import org.mcphoton.network.ProtocolWriteable;

/**
 * A NBT configuration.
 *
 * @see http://wiki.vg/NBT
 * @author TheElectronWill
 */
public class NbtConfiguration extends BaseConfiguration implements ProtocolWriteable {

	private volatile String name;

	public NbtConfiguration() {
		super();
		name = "";
	}

	public NbtConfiguration(Map<String, Object> data) {
		super(data);
		name = "";
	}

	public NbtConfiguration(Map<String, Object> data, String name) {
		super(data);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean containsByteArray(String key) {
		return get(key) instanceof byte[];
	}

	public boolean containsIntArray(String key) {
		return get(key) instanceof int[];
	}

	public byte[] getByteArray(String key) {
		return (byte[]) get(key);
	}

	public int[] getIntArray(String key) {
		return (int[]) get(key);
	}

	@Override
	public void readFrom(InputStream in) throws IOException {
		ReadTagCompound read = Nbt.read(in);
		this.name = read.name;
		this.map = read.data;
	}

	public void readFrom(ByteBuffer buff) throws IOException {
		DataInputBuffer dib = new DataInputBuffer(buff);
		ReadTagCompound read = new NbtReader(dib).read();
		this.name = read.name;
		this.map = read.data;
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		Nbt.write(map, name, out);
	}

	@Override
	public void writeTo(ProtocolOutputStream out) throws IOException {
		Nbt.write(map, name, out);
	}

}
