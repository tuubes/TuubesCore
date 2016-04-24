package org.mcphoton.impl.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * A MessageReader reads messages from a SocketChannel. A message is a block of data (bytes) sent over the
 * network. In the game protocol, each message is preceded by its size, encoded as a VarInt. The problem is
 * that the server may read multiple messages in once, or only a part of a message. The MessageReader solves
 * this problem: it can separate different messages and put several blocks of data together.
 *
 * @author TheElectronWill
 *
 */
public final class MessageReader {

	private final SocketChannel channel;
	private ByteBuffer buffer;
	private final int maxBufferSize;
	private int messageLength = -1, writePos = 0, readPos = 0;
	private boolean eos;
	private boolean readVarIntSucceed = false;

	public MessageReader(SocketChannel sc, int intialBufferSize, int maxBufferSize) {
		this.channel = sc;
		buffer = ByteBuffer.allocateDirect(intialBufferSize);
		this.maxBufferSize = maxBufferSize;
	}

	/**
	 * Tries to read the next message or to continue reading the current incomplete message. Returns
	 * <code>null</code> if all the message's bytes aren't available yet. In that case, this method should be
	 * called again
	 * later, when more data is available on the <code>SocketChannel</code>.<br />
	 * This method may reach the end of the stream. That can be checked by calling
	 * {@link #hasReachedEndOfStream()}.
	 *
	 * @return the message's data, or <code>null</code> if there aren't enough bytes yet.
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

		buffer.limit(writePos);//don't read what we didn't write in the buffer
		buffer.position(readPos);//start reading when we stopped last time

		if (messageLength == -1) {//we must read the message's length
			messageLength = tryReadVarInt();
			if (!readVarIntSucceed) {//fail
				return null;
			}
			readPos = buffer.position();
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
			return dataBuffer;
		}
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
			if (b >= 0) {// VarInt byte prefix is 0, it means that we just decoded the last 7 bits, therefore we've
				// finished.
				readVarIntSucceed = true;
				return i;
			}
		}
	}

}
