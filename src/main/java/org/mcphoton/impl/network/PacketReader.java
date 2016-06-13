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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import org.mcphoton.impl.server.Main;
import org.slf4j.Logger;

/**
 * A PacketReader reads packets from a SocketChannel. In the minecraft protocol, a packet is a block of data
 * (bytes) sent over the network, preceded by its size (encoded as a VarInt). With a SocketChannel's read, the
 * server may get multiple packets in once, or only a part of a packet. The PacketReader solves this problem:
 * it can separate different messages and regroup all the different parts of a message.
 *
 * @author TheElectronWill
 */
public final class PacketReader {

	private final SocketChannel channel;
	private ByteBuffer buffer;
	private final int maxBufferSize;
	private int messageLength = -1, writePos = 0, readPos = 0;
	private boolean eos;
	private boolean readVarIntSucceed = false;
	private final Logger logger = Main.serverInstance.logger;

	/**
	 * Creates a new PacketReader with the given parameters.
	 *
	 * @param sc the SocketChannel to read the data from.
	 * @param intialBufferSize the initial size of the internal buffer, which contains the read packet(s)
	 * @param maxBufferSize the maximum size of the internal buffer. This limit the size of the incoming
	 * packets.
	 */
	public PacketReader(SocketChannel sc, int intialBufferSize, int maxBufferSize) {
		this.channel = sc;
		buffer = ByteBuffer.allocateDirect(intialBufferSize);
		this.maxBufferSize = maxBufferSize;
	}

	/**
	 * Tries to read the next packet or to continue reading the current incomplete packet. Returns
	 * <code>null</code> if all the packet's bytes aren't available yet. In that case, this method should be
	 * called again later, when more data is available on the <code>SocketChannel</code>.
	 * <p>
	 * This method may reach the end of the stream. That can be checked by calling
	 * {@link #hasReachedEndOfStream()}.
	 * </p>
	 *
	 * @return the packet's data, or <code>null</code> if there aren't enough bytes yet.
	 */
	public ByteBuffer readNext() throws IOException {
		if (buffer.remaining() < 5) {//5 bytes threshold
			buffer.position(readPos);//prepare for compacting
			buffer.limit(writePos);//prepare for compacting
			buffer.compact();//compacts the buffer
			readPos = 0;
			writePos = buffer.position();
		} else {
			buffer.limit(buffer.capacity());//prepare to write the data. This is in the "else" because buffer.compact() already changes the limit
		}

		buffer.position(writePos);//prepare to write in the buffer
		int read = channel.read(buffer);//reads more data from the channel and writes it to the buffer
		if (read == -1) {//channel closed
			eos = true;
			return null;
		}
		writePos = buffer.position();
		logger.trace("writePos {}", writePos);

		buffer.limit(writePos);//don't read what we didn't write in the buffer
		buffer.position(readPos);//start reading when we stopped last time

		if (messageLength == -1) {//we must read the message's length
			messageLength = tryReadVarInt();
			logger.trace("messageLength {}", messageLength);
			if (!readVarIntSucceed) {//fail
				logger.trace("read var int FAIL");
				return null;
			}
			readPos = buffer.position();
			logger.trace("readPos  {}", readPos);
			if (buffer.capacity() < messageLength) {//buffer to small
				if (messageLength > maxBufferSize) {
					throw new IOException("Message too big. Its size (" + messageLength + " bytes) is over the limit (" + maxBufferSize + " bytes )");
				}
				int bufferLength = (int) Math.ceil(messageLength / 32.0) * 32;//round to the next multiple of 32
				buffer = ByteBuffer.allocateDirect(bufferLength);//create a new ByteBuffer with enough space for the entire message
			}
		}
		if (buffer.remaining() >= messageLength) {//the message is entirely available
			int packetEnd = buffer.position() + messageLength;//go to the end of the message
			readPos = packetEnd;//set the reading position
			buffer.limit(packetEnd);//set the limit to prevent dataBuffer to access other messages
			ByteBuffer dataBuffer = buffer.slice();//shares the message's data
			messageLength = -1;//reset messageLength to -1 to read it the next time this method is called
			logger.trace("SUCCESS  -> readPos {} and writePos {}", readPos, writePos);
			return dataBuffer;
		}
		logger.trace("NOT ENOUGH DATA");
		return null;
	}

	/**
	 * Checks if the end of the stream has been reached.
	 */
	public boolean hasReachedEndOfStream() {
		return eos;
	}

	/**
	 * Tries to read a varInt, and updates the {@link #readVarIntSucceed} field.
	 *
	 * @return the varInt value, or -1 if it failed
	 */
	private int tryReadVarInt() {
		buffer.mark();
		int shift = 0, i = 0;
		while (true) {
			if (!buffer.hasRemaining()) {
				buffer.reset();
				readVarIntSucceed = false;
				return -1;
			}
			byte b = buffer.get();
			i |= (b & 0x7F) << shift;// Remove sign bit and shift to get the next 7 bits
			shift += 7;
			if (b >= 0) {// VarInt byte prefix is 0, it means that we just decoded the last 7 bits, therefore we've finished.
				readVarIntSucceed = true;
				return i;
			}
		}
	}

}
