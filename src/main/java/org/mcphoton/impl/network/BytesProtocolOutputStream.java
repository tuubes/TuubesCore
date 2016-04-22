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
public final class BytesProtocolOutputStream extends ProtocolOutputStream {

	private byte[] buff;
	private int count = 5;

	/**
	 * Creates a new stream with the initial capacity of 37 bytes. The first 5 bytes are reserved for the
	 * varInt that indicate the message's size, so the actual capacity for the data is initially 32 bytes.
	 */
	public BytesProtocolOutputStream() {
		buff = new byte[32];
	}

	/**
	 * Creates a new stream with the specified initial capacity. The first 5 bytes are reserved for the
	 * varInt that indicate the message's size.
	 */
	public BytesProtocolOutputStream(int initialCapacity) {
		buff = new byte[initialCapacity];
	}

	/**
	 * Creates a new stream with the specified data. The first 5 bytes are reserved for the
	 * varInt that indicate the message's size.
	 */
	public BytesProtocolOutputStream(byte[] data) {
		buff = data;
	}

	/**
	 * Returns a ByteBuffer that contains the message's size as a varint, followed by the message's data. The
	 * message's data is directly shared with the underlying byte array of this stream.
	 */
	public ByteBuffer asMessageBuffer() {
		int messageSize = getMessageSize();
		int initialPos = 5 - ProtocolHelper.varIntSize(messageSize);
		ByteBuffer buffer = ByteBuffer.wrap(buff, initialPos, count);
		ProtocolHelper.writeVarInt(messageSize, buffer);
		buffer.position(0);//resets the position
		return buffer;
	}

	public int getMessageSize() {
		return count - 5;
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
	 * Clears this ProtocolOutputStream, that is, sets it size (internal counter) to 0. The capacity does not
	 * change.
	 */
	@Override
	public void clear() {
		count = 0;
	}

	/**
	 * Resets this ProcotolOutputStream, that is, delete the internal buffer and create a new one with the
	 * default capacity (32 bytes).
	 */
	@Override
	public void reset() {
		buff = new byte[32];
	}

	/**
	 * Resets this ProcotolOutputStream, that is, delete the internal buffer and create a new one with the
	 * specified capacity.
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
