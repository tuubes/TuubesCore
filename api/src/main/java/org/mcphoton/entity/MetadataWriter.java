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

import java.io.IOException;
import java.util.UUID;
import org.mcphoton.item.ItemStack;
import org.mcphoton.messaging.ChatMessage;
import org.mcphoton.network.ProtocolHelper;
import org.mcphoton.network.ProtocolOutputStream;
import org.mcphoton.utils.EulerAngles;
import org.mcphoton.utils.Location;

/**
 *
 * @author TheElectronWill
 */
public final class MetadataWriter {

	private final ProtocolOutputStream out;

	public MetadataWriter(ProtocolOutputStream out) {
		this.out = out;
	}

	public void writeAbsentPosition(int index) {
		out.writeByte(index);
		out.writeByte(9);
		out.writeBoolean(false);
	}

	public void writeAbsentUUID(int index) {
		out.writeByte(index);
		out.writeByte(11);
		out.writeBoolean(false);
	}

	public void writeBlockId(int index, int id) {
		out.writeByte(index);
		out.writeByte(12);
		out.writeVarInt(id);
	}

	public void writeBoolean(int index, boolean b) {
		out.writeByte(index);
		out.writeByte(6);
		out.writeBoolean(b);
	}

	public void writeByte(int index, int b) {
		out.writeByte(index);
		out.writeByte(0);
		out.writeByte(b);
	}

	public void writeChat(int index, ChatMessage chat) {
		out.writeByte(index);
		out.writeByte(4);
		out.writeString(chat.toString());
	}

	public void writeDirection(int index, int d) {
		out.writeByte(index);
		out.writeByte(10);
		out.writeVarInt(d);
	}

	public void writeFloat(int index, float f) {
		out.writeByte(index);
		out.writeByte(2);
		out.writeFloat(f);
	}

	public void writeOptionalPosition(int index, int x, int y, int z) {
		out.writeByte(index);
		out.writeByte(9);
		out.writeBoolean(true);
		out.writeLong(ProtocolHelper.encodePosition(x, y, z));
	}

	public void writeOptionalPosition(int index, Location l) {
		if (l == null) {
			writeAbsentPosition(index);
		} else {
			writeOptionalPosition(index, l.getBlockX(), l.getBlockY(), l.getBlockZ());
		}
	}

	public void writeOptionalUUID(int index, long msb, long lsb) {
		out.writeByte(index);
		out.writeByte(11);
		out.writeBoolean(true);
		out.writeLong(msb);
		out.writeLong(lsb);
	}

	public void writeOptionalUUID(int index, UUID uuid) {
		if (uuid == null) {
			writeAbsentUUID(index);
		} else {
			writeOptionalUUID(index, uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
		}
	}

	public void writePosition(int index, int x, int y, int z) {
		out.writeByte(index);
		out.writeByte(8);
		out.writeLong(ProtocolHelper.encodePosition(x, y, z));
	}

	public void writePosition(int index, Location l) {
		writePosition(index, l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}

	public void writeRotation(int index, float rx, float ry, float rz) {
		out.writeByte(index);
		out.writeByte(7);
		out.writeFloat(rx);
		out.writeFloat(ry);
		out.writeFloat(rz);
	}

	public void writeRotation(int index, EulerAngles angles) {
		writeRotation(index, angles.getPitch(), angles.getYaw(), angles.getRoll());
	}

	public void writeSlot(int index, ItemStack stack) {
		out.writeByte(index);
		out.writeByte(5);
		try {
			stack.writeTo(out);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void writeString(int index, String s) {
		out.writeByte(index);
		out.writeByte(3);
		out.writeString(s);
	}

	public void writeVarInt(int index, int i) {
		out.writeByte(index);
		out.writeByte(1);
		out.writeVarInt(i);
	}

	public void writeEnd() {
		out.writeByte(0xff);
	}

}
