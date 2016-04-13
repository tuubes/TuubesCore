package org.mcphoton.impl.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;
import org.mcphoton.network.ProtocolHelper;

/**
 * A MessageWriter is able to writes messages to a SocketChannel, in several times (with multiple incomplete writes) if
 * necessary. <b>A MessageWriter is NOT thread-safe</b>
 * 
 * @author TheElectronWill
 * 		
 */
public final class MessageWriter {
	
	private final ByteBuffer varIntBuff = ByteBuffer.allocateDirect(5);
	private final SocketChannel sc;
	private final Queue<ByteBuffer> pendingMessages = new ArrayDeque<>(4);
	private boolean readyToWriteVarIntBuff = false, needToWriteVarIntBuff = true;
	
	public MessageWriter(SocketChannel sc) {
		this.sc = sc;
	}
	
	/**
	 * Adds a ByteBuffer that needs to be written to the <code>MessafeWriter</code>'s <code>SocketChannel</code> to an
	 * internal Queue. The ByteBuffer position should be 0 and its limit must mark the end of the data, so that
	 * <code>messageData.limit()-messageDara.position() = the size of the data</code>.
	 * 
	 * @param messageData the ByteBuffer that contains the data of a message.
	 */
	public void addForWriting(ByteBuffer messageData) {
		pendingMessages.add(messageData);
	}
	
	/**
	 * Writes a message as soon as possible. This method first check if they are messages waiting for being completely
	 * written. If they are, this method simply calls {@link #addForWriting(ByteBuffer)} and returns false. If they
	 * aren't any message that is waiting for being completely written, this method tries to write it immediatly. If the
	 * write is complete it returns <code>true</code>, if the write is incomplete it (among other things) calls
	 * {@link #addForWriting(ByteBuffer)}.
	 * <h1>Conditions on the ByteBuffer <code>messageData</code></h1>
	 * <p>
	 * The ByteBuffer position should be 0 and its limit must mark the end of the data, so that
	 * <code>messageData.limit()-messageDara.position() = the size of the data</code>.
	 * </p>
	 * 
	 * @param messageData the ByteBuffer that contains the data of a message.
	 * @return <code>true</code> if the message has been immediatly and completely written, <code>false</code>
	 *         otherwise.
	 * @throws IOException
	 */
	public boolean writeASAP(ByteBuffer messageData) throws IOException {
		if (!pendingMessages.isEmpty()) {
			addForWriting(messageData);
			return false;
		}
		
		// --- Writes the message's length (varIntBuff) ---
		ProtocolHelper.writeVarInt(messageData.limit(), varIntBuff);
		varIntBuff.flip();
		sc.write(varIntBuff);
		if (varIntBuff.hasRemaining()) {// incomplete write
			readyToWriteVarIntBuff = needToWriteVarIntBuff = true;
			addForWriting(messageData);
			return false;
		} else {
			needToWriteVarIntBuff = false;
		}
		
		// --- Writes the message's data ---
		sc.write(messageData);
		if (messageData.hasRemaining()) {// incomplete write
			addForWriting(messageData);
			return false;
		} else {
			needToWriteVarIntBuff = readyToWriteVarIntBuff = false;
			varIntBuff.clear();
			pendingMessages.remove();
			return true;
		}
	}
	
	/**
	 * Tries to write all the pending messages to the <code>SocketChannel</code>. An attempt is made to write as much
	 * message's data as possible. The caller of this method should check the returned boolean to determine if it needs
	 * to be re-called later.
	 * 
	 * @return <code>true</code> if all the data has been written, <code>false</code> otherwise.
	 * @throws IOException
	 */
	public boolean writeMessagesToChannel() throws IOException {
		while (true) {
			ByteBuffer pendingMessage = pendingMessages.peek();
			if (pendingMessage == null)
				return true;
				
			// --- Writes the message's length (varIntBuff) ---
			if (needToWriteVarIntBuff) {
				if (!readyToWriteVarIntBuff) {
					ProtocolHelper.writeVarInt(pendingMessage.limit(), varIntBuff);
					varIntBuff.flip();
					readyToWriteVarIntBuff = true;
				}
				sc.write(varIntBuff);
				if (varIntBuff.hasRemaining()) {// incomplete write
					return false;
				} else {
					needToWriteVarIntBuff = false;
				}
			}
			
			// --- Writes the message's data ---
			sc.write(pendingMessage);
			if (pendingMessage.hasRemaining()) {// incomplete write
				return false;
			} else {// complete write of the varIntBuff and of the message's data
				needToWriteVarIntBuff = readyToWriteVarIntBuff = false;
				varIntBuff.clear();
				pendingMessages.remove();
				return true;
			}
		}
	}
	
}
