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
