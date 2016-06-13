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
import java.nio.charset.StandardCharsets;
import org.mcphoton.network.ProtocolHelper;
import org.mcphoton.network.ProtocolOutputStream;

/**
 * A ProtocolOutputStream based on a byte array.
 *
 * @author TheElectronWill
 *
 */
public final class ArrayProtocolOutputStream extends ProtocolOutputStream {

	private byte[] buff;
	private int count = 10;

	/**
	 * Creates a new stream with the initial capacity of 42 bytes. The first 10 bytes are reserved for the
	 * two varInts that indicate the packet's size and the packet's id, so the actual capacity for the data is
	 * initially 32 bytes.
	 */
	public ArrayProtocolOutputStream() {
		buff = new byte[42];
	}

	/**
	 * Creates a new stream with the specified initial capacity. The first 10 bytes are reserved for the
	 * two varInts that indicate the packet's size and the packet's id.
	 */
	public ArrayProtocolOutputStream(int initialCapacity) {
		buff = new byte[initialCapacity];
	}

	/**
	 * Creates a new stream with the specified data. The first 10 bytes are reserved for the
	 * two varInts that indicate the packet's size and the packet's id.
	 */
	public ArrayProtocolOutputStream(byte[] data) {
		buff = data;
	}

	/**
	 * Returns a ByteBuffer that contains the message's size (as a varint) followed by the packet's id (as a
	 * varint) and the message's data. The message's data is directly shared with the underlying byte
	 * array of this stream.
	 */
	public ByteBuffer asPacketBuffer(int packetId) {
		//-- Determines the packet's size --
		int idIntSize = ProtocolHelper.varIntSize(packetId);
		int messageSize = getDataSize() + idIntSize;
		int messageIntSize = ProtocolHelper.varIntSize(messageSize);

		ByteBuffer buffer = ByteBuffer.wrap(buff, 0, count);//puts the data into a ByteBuffer

		//-- Appends the varInts at the beginning of the ByteBuffer --
		int initialPos = 10 - messageIntSize - idIntSize;
		buffer.position(initialPos);
		ProtocolHelper.writeVarInt(messageSize, buffer);
		ProtocolHelper.writeVarInt(packetId, buffer);
		buffer.position(initialPos);//resets the position

		return buffer;
	}

	public int getDataSize() {
		return count - 10;
	}

	@Override
	public int size() {
		return count;
	}

	@Override
	public int capacity() {
		return buff.length;
	}

	/**
	 * Clears this ProtocolOutputStream: sets its data size to 0, that is, its actual size to 10 (the first
	 * ten bytes are reserved). The capacity does not change.
	 */
	@Override
	public void clear() {
		count = 10;
	}

	/**
	 * Resets this ProcotolOutputStream, that is, delete the internal buffer and create a new one with the
	 * default capacity (42 bytes with 10 reserved).
	 */
	@Override
	public void reset() {
		buff = new byte[42];
	}

	/**
	 * Resets this ProcotolOutputStream, that is, delete the internal buffer and create a new one with the
	 * specified capacity. 10 bytes will be reserved at the beginning of the buffer.
	 */
	public void reset(int newCapacity) {
		buff = new byte[newCapacity];
	}

	private void ensureCapacity(int cap) {
		if (buff.length < cap) {
			byte[] buff2 = new byte[Math.max(cap, buff.length * 2)];
			System.arraycopy(buff, 0, buff2, 0, count);
			buff = buff2;
		}
	}

	private void directWrite(int b) {
		buff[count++] = (byte) b;
	}

	@Override
	public void write(int b) {
		writeByte(b);
	}

	/**
	 * Writes a boolean as a single byte. Its value is 1 if true and 0 if false.
	 */
	@Override
	public void writeBoolean(boolean b) {
		writeByte(b ? 1 : 0);
	}

	@Override
	public void writeByte(int b) {
		ensureCapacity(buff.length + 1);
		directWrite(b);
	}

	@Override
	public void writeShort(int s) {
		ensureCapacity(buff.length + 2);
		directWrite(s >> 8);
		directWrite(s);
	}

	@Override
	public void writeChar(int c) {
		ensureCapacity(buff.length + 2);
		directWrite(c >> 8);
		directWrite(c);
	}

	@Override
	public void writeInt(int i) {
		ensureCapacity(buff.length + 4);
		directWrite(i >> 24);
		directWrite(i >> 16);
		directWrite(i >> 8);
		directWrite(i);
	}

	@Override
	public void writeLong(long l) {
		ensureCapacity(buff.length + 8);
		directWrite((byte) (l >> 56));
		directWrite((byte) (l >> 48));
		directWrite((byte) (l >> 40));
		directWrite((byte) (l >> 32));
		directWrite((byte) (l >> 24));
		directWrite((byte) (l >> 16));
		directWrite((byte) (l >> 8));
		directWrite((byte) l);
	}

	@Override
	public void writeFloat(float f) {
		writeInt(Float.floatToIntBits(f));
	}

	@Override
	public void writeDouble(double d) {
		writeLong(Double.doubleToLongBits(d));
	}

	/**
	 * Writes a String with the UTF-8 charset, prefixed with its size (in bytes) encoded as a VarInt.
	 */
	@Override
	public void writeString(String s) {
		byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
		writeVarInt(bytes.length);
		write(bytes);
	}

	@Override
	public void writeVarInt(int i) {
		while ((i & 0xFFFF_FF80) != 0) {// While we have more than 7 bits (0b0xxxxxxx)
			byte data = (byte) (i | 0x80);// Discard bit sign and set msb to 1 (VarInt byte prefix).
			writeByte(data);
			i >>>= 7;
		}
		writeByte((byte) i);
	}

	@Override
	public void writeVarLong(long l) {
		while ((l & 0xFFFF_FFFF_FFFF_FF80l) != 0) {// While we have more than 7 bits (0b0xxxxxxx)
			byte data = (byte) (l | 0x80);// Discard bit sign and set msb to 1 (VarInt byte prefix).
			writeByte(data);
			l >>>= 7;
		}
		writeByte((byte) l);
	}

	@Override
	public void write(byte[] b, int off, int len) {
		ensureCapacity(buff.length + len);
		System.arraycopy(b, off, buff, count, len);
		count += len;
	}

	@Override
	public void write(byte[] b) {
		write(b, 0, b.length);
	}

	/**
	 * Does nothing.
	 */
	@Override
	public void close() {
	}

	/**
	 * Does nothing.
	 */
	@Override
	public void flush() {
	}

}
