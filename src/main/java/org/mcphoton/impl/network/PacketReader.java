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
import org.slf4j.LoggerFactory;
import org.slf4j.impl.PhotonLogger;

/**
 * A PacketReader reads packets from a SocketChannel. In the minecraft protocol, a packet is a block of data
 * (bytes) sent over the network, preceded by its size (encoded as a VarInt). With a SocketChannel's read, the
 * server may get multiple packets in once, or only a part of a packet. The PacketReader solves this problem:
 * it can separate different messages and regroup all the different parts of a message.
 * <p>
 * This class isn't thread-safe. A client's PacketReader should only be used in the NIO network thread.
 * </p>
 *
 * @author TheElectronWill
 */
public final class PacketReader {

	private static final Logger logger = LoggerFactory.getLogger("PacketReader");

	/**
	 * An empty ByteBuffer with a capacity of 0, used to indicate that the end of the stream has been reached.
	 */
	public static final ByteBuffer END_OF_STREAM = ByteBuffer.allocate(0);

	private final SocketChannel channel;
	private final int maxBufferSize;
	private int packetLength = -1, writePos = 0, readPos = 0;
	private ByteBuffer buffer;
	private Codec cipherCodec;

	public PacketReader(SocketChannel channel, int initialBufferSize, int maxBufferSize) {
		this(channel, initialBufferSize, maxBufferSize, new NoCodec());
	}

	public PacketReader(SocketChannel channel, int initialBufferSize, int maxBufferSize, Codec cipherCodec) {
		this.channel = channel;
		this.cipherCodec = cipherCodec;
		this.maxBufferSize = maxBufferSize;
		this.buffer = ByteBuffer.allocateDirect(initialBufferSize);
		((PhotonLogger) logger).setLevel(Main.serverInstance.logger.getLevel());
	}

	/**
	 * Compacts the buffer, that is, moves the bytes between readPos and writePos to the beginning of the
	 * buffer. Then, sets readPos to 0 and update writePos accordingly.
	 */
	private void compactBuffer() {
		logger.trace("compactBuffer()");
		buffer.position(readPos);
		buffer.limit(writePos);
		buffer.compact();
		readPos = 0;
		writePos = buffer.position();
	}

	/**
	 * Allocates a bigger buffer with a capacity up to {@link #maxBufferSize}. If the packet's length is
	 * greater than <code>maxBufferSize</code>, an IOException is thrown.
	 *
	 * @throws IOException if an I/O error occurs, or if the packet's length is greater than the maximum
	 * buffer size.
	 */
	private void allocateBiggerBuffer() throws IOException {
		logger.trace("allocateBiggerBuffer()");
		if (packetLength > maxBufferSize) {
			throw new IOException("Packet too big: size is " + packetLength + ", maximum is " + maxBufferSize);
		}
		ByteBuffer newBuffer = ByteBuffer.allocateDirect(packetLength);
		buffer.position(readPos);
		newBuffer.put(buffer);
		readPos = 0;
		writePos = newBuffer.position();
		buffer = newBuffer;
	}

	/**
	 * Reads more data from the socket channel and puts it in the buffer at the writePos position.
	 *
	 * @return the number of bytes read, of -1 if the end of the stream has been reached.
	 * @throws IOException if an I/O error occurs.
	 */
	private int readFromSocketChannel() throws IOException {
		logger.trace("readFromSocketChannel() begins with writePos={}", writePos);
		buffer.position(writePos);
		int read = channel.read(buffer);
		writePos += read;
		logger.trace("readFromSocketChannel() ends with writePos={}", writePos);
		return read;
	}

	/**
	 * Reads the next available VarInt and sets the value of the {@link #packetLength} field.
	 * {@link #packetLength} will be -1 if the end of stream is reached, and -2 if not enough bytes are
	 * available.
	 *
	 * @throws IOException if an I/O error occurs.
	 */
	private void readPacketLength() throws IOException {
		packetLength = tryReadVarInt();
		if (packetLength == -2) {
			if (buffer.capacity() - writePos < 5) {
				compactBuffer();
			}
			int read = readFromSocketChannel();
			if (read == -1) {//end of stream
				packetLength = -1;
			} else {
				packetLength = tryReadVarInt();
			}
		}
	}

	/**
	 * Reads the packet's data, assuming the packet's length is already known.
	 *
	 * @param tryReadingFromSocketChannel true to read more data from the socket if needed, false to return
	 * null if there isn't enough data.
	 * @return a ByteBuffer containing the packet's data, or <code>null</code> if more data is needed, or
	 * {@link #END_OF_STREAM} if the end of the stream has been reached.
	 * @throws IOException IOException if an I/O error occurs.
	 */
	private ByteBuffer readPacketData(boolean tryReadingFromSocketChannel) throws IOException {
		if (writePos - readPos >= packetLength) {//all the data is available
			//Prepare to slice the buffer:
			buffer.position(readPos);
			buffer.limit(readPos + packetLength);
			ByteBuffer dataBuffer = buffer.slice();//dataBuffer can only access to the data of this packet.

			//Decrypt data with the cipher codec:
			try {
				cipherCodec.decode(dataBuffer);
			} catch (Exception ex) {
				throw new IOException("Error while decrypting incomig packet", ex);
			}

			//Prepare to read the next packet:
			readPos += packetLength;
			packetLength = -1;
			buffer.limit(buffer.capacity());

			return dataBuffer;//return the packet's data.
		} else if (tryReadingFromSocketChannel) {
			if (buffer.capacity() < packetLength) {//need a bigger buffer
				allocateBiggerBuffer();
			} else if (buffer.capacity() - readPos < packetLength) {//need to compact the buffer
				compactBuffer();
			}
			int read = readFromSocketChannel();
			if (read == -1) {//end of stream
				return END_OF_STREAM;
			}
			return readPacketData(false);//retry
		}
		return null;
	}

	/**
	 * Tries to read the next packet from the SocketChannel. If all the packet's bytes aren't available yet,
	 * this method returns <code>null</code> and should be called again later, when more data is available on
	 * the <code>SocketChannel</code>.
	 * <p>
	 * This method may reach the end of the stream. That can be checked by calling
	 * {@link #hasReachedEndOfStream()}.
	 * </p>
	 *
	 * @return a ByteBuffer containing the packet's data, or <code>null</code>.
	 * @throws IOException if an I/O error occurs.
	 */
	public ByteBuffer readNext() throws IOException {
		logger.trace("readNext() with packetLength={} writePos={} and readPos={}", packetLength, writePos, readPos);
		if (packetLength < 0) {//need to read the packet's length
			readPacketLength();
			if (packetLength == -1) {//end of stream
				return END_OF_STREAM;
			} else if (packetLength == -2) {//not enough bytes available
				return null;
			}
		}
		logger.trace("packetLength={}", packetLength);
		return readPacketData(true);//reads the packet's data
	}

	/**
	 * Sets the cipher codec for every packet read from now on.
	 */
	public void setCipherCodec(Codec cipherCodec) {
		this.cipherCodec = cipherCodec;
	}

	/**
	 * Tries to read a VarInt.
	 *
	 * @return the VarInt value, or -2 if the operation failed.
	 */
	private int tryReadVarInt() {
		buffer.position(readPos);
		int shift = 0, i = 0;
		while (true) {
			if (buffer.position() >= writePos) {//no more bytes available
				buffer.position(readPos);//reset the position
				return -2;
			}
			byte b = buffer.get();
			i |= (b & 0x7F) << shift;// Remove sign bit and shift to get the next 7 bits
			shift += 7;
			if (b >= 0) {// VarInt byte prefix is 0, it means that we just decoded the last 7 bits, therefore we've finished.
				readPos = buffer.position();//take the read of the VarInt into account
				return i;
			}
		}
	}

}
