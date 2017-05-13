package com.electronwill.utils;

import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A simple DataInput implementation based on a ByteBuffer.
 *
 * @author TheElectronWill
 */
public class DataInputBuffer implements DataInput {
	protected final ByteBuffer buff;

	/**
	 * Creates a new DataInputBuffer based on the specified ByteBuffer.
	 *
	 * @param buff the ByteBuffer to use.
	 */
	public DataInputBuffer(ByteBuffer buff) {
		this.buff = buff;
	}

	@Override
	public void readFully(byte[] b) throws IOException {
		buff.get(b);
	}

	@Override
	public void readFully(byte[] b, int off, int len) throws IOException {
		buff.get(b, off, len);
	}

	@Override
	public int skipBytes(int n) throws IOException {
		int skip = Math.max(n, buff.remaining());
		buff.position(buff.position() + skip);
		return skip;
	}

	@Override
	public boolean readBoolean() throws IOException {
		return buff.get() == 1;
	}

	@Override
	public byte readByte() throws IOException {
		return buff.get();
	}

	@Override
	public int readUnsignedByte() throws IOException {
		return buff.get() & 0xff;
	}

	@Override
	public short readShort() throws IOException {
		return buff.getShort();
	}

	@Override
	public int readUnsignedShort() throws IOException {
		return buff.getShort() & 0xffff;
	}

	@Override
	public char readChar() throws IOException {
		return buff.getChar();
	}

	@Override
	public int readInt() throws IOException {
		return buff.getInt();
	}

	@Override
	public long readLong() throws IOException {
		return buff.getLong();
	}

	@Override
	public float readFloat() throws IOException {
		return buff.getFloat();
	}

	@Override
	public double readDouble() throws IOException {
		return buff.getDouble();
	}

	@Deprecated
	@Override
	public String readLine() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String readUTF() throws IOException {
		return java.io.DataInputStream.readUTF(this);
	}
}