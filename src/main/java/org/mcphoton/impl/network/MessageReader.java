package org.mcphoton.impl.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * A MessageReader reads messages from a SocketChannel.
 * <h2>What is a "message"</h2>
 * <p>
 * A "messsage" is a block of data sent over the network, by the client to the server, or by the server to the client.
 * This block of data is preceded by its size, encoded as a VarInt number with a variable number of bytes (google
 * "VarInt" for more informations).
 * </p>
 * 
 * @author TheElectronWill
 * 		
 */
public final class MessageReader {
	
	private final SocketChannel sc;
	private ByteBuffer buff = ByteBuffer.allocateDirect(256);
	private int writePosition = 0, readPosition = 0;
	private int dataSize = -1;
	private boolean endOfStream;
	private boolean readVarIntSucceed = false;
	
	public MessageReader(SocketChannel sc) {
		this.sc = sc;
	}
	
	/**
	 * Tries to read the next message or to continue reading the current incomplete message. Returns <code>null</code>
	 * if all the message's bytes aren't available yet. In that case, this method should be called again later, when
	 * more data is available on the <code>SocketChannel</code>.
	 * <p>
	 * <b>Caution: </b>This method may reach the end of the stream. That can be checked by calling
	 * {@link #hasReachedEndOfStream()}.
	 * </p>
	 *
	 * @return the message's data, or <code>null</code> if there aren't enough bytes yet.
	 */
	public ByteBuffer readMore() throws IOException {
		// --- Reads more bytes ---
		buff.position(writePosition);
		int read = sc.read(buff);
		if (read == -1) {// end of stream
			endOfStream = true;
			return null;
		} else if (read == 0) {// nothing read
			return null;
		}
		writePosition = buff.position();
		
		// --- Reads dataSize if needed ---
		if (dataSize == -1) {
			buff.position(readPosition);
			buff.limit(writePosition);// to read only the available bytes
			int varInt = tryReadVarInt();
			buff.limit(buff.capacity());// resets the limit to the buffer's capacity
			
			if (readVarIntSucceed) {
				readPosition = buff.position();// readPosition = after the varInt, at the beginning of the message's
												// data
			} else {
				buff.position(readPosition);// resets the position at beginning of the varInt
				return null;
			}
			if (varInt <= 0)
				throw new IOException("Invalid data size: " + varInt);
				
			dataSize = varInt;// updates the data size
		}
		
		// --- Reads the message's data ---
		buff.position(readPosition);
		if (writePosition - readPosition >= dataSize) {// all the data of this message has been received
			buff.limit(readPosition + dataSize);
			ByteBuffer data = buff.slice();// create a subsequence [readPosition, readPosition + dataSize]
			buff.limit(buff.capacity());// resets the limit to the buffer's capacity
			return data;
		}
		if (buff.capacity() - readPosition < dataSize) {// not enough remaining space
			if (buff.capacity() < dataSize) {// buffer too small
				ByteBuffer buff2 = ByteBuffer.allocateDirect(dataSize);
				buff.limit(writePosition);
				buff2.put(buff);
				buff = buff2;
			} else {// needs compacting
				buff.limit(writePosition);
				buff.compact();
				writePosition = buff.position();
				readPosition = 0;
			}
		} // else: not enough space but not enough data
		return null;
	}
	
	/**
	 * Checks if the end of the stream has been reached.
	 */
	public boolean hasReachedEndOfStream() {
		return endOfStream;
	}
	
	/**
	 * Tries to read a varInt, and updates the {@link #readVarIntSucceed} field according to wether it succeed or not.
	 * 
	 * @return the varInt value
	 */
	private int tryReadVarInt() {
		int shift = 0, i = 0;
		while (true) {
			if (!buff.hasRemaining()) {
				readVarIntSucceed = false;
				return 0;
			}
			byte b = (byte) buff.get();
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
