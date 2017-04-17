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
package org.mcphoton.entity;

import java.util.Arrays;
import java.util.UUID;
import org.mcphoton.messaging.ChatMessage;
import org.mcphoton.network.ByteArrayProtocolOutputStream;
import org.mcphoton.network.ProtocolHelper;

/**
 *
 * @author TheElectronWill
 */
public class MetadataBuilder {

	protected final ByteArrayProtocolOutputStream out;

	public MetadataBuilder() {
		this(16);
	}

	public MetadataBuilder(int initialCapacity) {
		this.out = new ByteArrayProtocolOutputStream(initialCapacity);
	}

	public MetadataBuilder putByte(byte b) {
		out.writeByte(out.size());//index
		out.writeByte(0);//type
		out.writeByte(b);//data
		return this;
	}

	public MetadataBuilder putVarInt(int i) {
		out.writeByte(out.size());
		out.writeByte(1);
		out.writeVarInt(i);
		return this;
	}

	public MetadataBuilder putFloat(float f) {
		out.writeByte(out.size());
		out.writeByte(2);
		out.writeFloat(f);
		return this;
	}

	public MetadataBuilder putString(String s) {
		out.writeByte(out.size());
		out.writeByte(3);
		out.writeString(s);
		return this;
	}

	public MetadataBuilder putChatMessage(ChatMessage msg) {
		out.writeByte(out.size());
		out.writeByte(4);
		out.writeString(msg.toString());
		return this;
	}

	//TODO putSlot
	//
	public MetadataBuilder putBoolean(boolean b) {
		out.writeByte(out.size());
		out.writeByte(6);
		out.writeBoolean(b);
		return this;
	}

	public MetadataBuilder putRotation(float rx, float ry, float rz) {
		out.writeByte(out.size());
		out.writeByte(7);
		out.writeFloat(rx);
		out.writeFloat(ry);
		out.writeFloat(rz);
		return this;
	}

	public MetadataBuilder putPosition(int x, int y, int z) {
		out.writeByte(out.size());
		out.writeByte(8);
		out.writeLong(ProtocolHelper.encodePosition(x, y, z));
		return this;
	}

	public MetadataBuilder putOptionalPosition(int x, int y, int z) {
		out.writeByte(out.size());
		out.writeByte(9);
		out.writeBoolean(true);
		out.writeLong(ProtocolHelper.encodePosition(x, y, z));
		return this;
	}

	public MetadataBuilder putDirection(int d) {
		out.writeByte(out.size());
		out.writeByte(10);
		out.writeVarInt(d);
		return this;
	}

	public MetadataBuilder putOptionalUUID(UUID uuid) {
		out.writeByte(out.size());
		out.writeByte(11);
		out.writeBoolean(true);
		out.writeLong(uuid.getMostSignificantBits());
		out.writeLong(uuid.getLeastSignificantBits());
		return this;
	}

	public MetadataBuilder putBlockId(int id) {
		out.writeByte(out.size());
		out.writeByte(12);
		out.writeVarInt(id);
		return this;
	}

	/**
	 * Gets the size of the metadata, in bytes.
	 */
	public int getSize() {
		return out.size();
	}

	/**
	 * Construct a byte array that contains the metadata's bytes.
	 *
	 * @param trim true to ensure that the returned array's length is equal to the result of "getSize()",
	 * false to return directly the underlying byte array, which may be bigger than the metadata size.
	 */
	public byte[] buildByteArray(boolean trim) {
		return trim ? Arrays.copyOf(out.getBytes(), out.size()) : out.getBytes();
	}

}
