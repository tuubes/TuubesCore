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
import java.util.LinkedList;
import java.util.Queue;
import org.mcphoton.network.Packet;

/**
 * A MessageWriter writes messages to a SocketChannel. It handles incomplete writes.
 *
 * @author TheElectronWill
 *
 */
public final class MessageWriter {

	private final SocketChannel channel;
	private final Queue<Packet> pendingMessages = new LinkedList<>();
	private final BytesProtocolOutputStream out = new BytesProtocolOutputStream();
	private ByteBuffer currentBuffer;

	public MessageWriter(SocketChannel sc) {
		this.channel = sc;
	}

	/**
	 * Adds a message to the queue of pending messages, to write it later.
	 */
	public boolean enqueue(Packet message) {
		return pendingMessages.offer(message);
	}

	/**
	 * Writes a message as soon as possible.
	 *
	 * @return true if the message has been completely written immediatly, false otherwise.
	 * @throws IOException
	 */
	public boolean writeASAP(Packet message) throws IOException {
		if (!pendingMessages.isEmpty() || currentBuffer != null && currentBuffer.hasRemaining()) {
			//cannot write now because another message is being written
			enqueue(message);
			return false;
		}

		message.writeTo(out);
		currentBuffer = out.asPacketBuffer(message.getId());
		currentBuffer.position(0);

		channel.write(currentBuffer);
		if (currentBuffer.hasRemaining()) {//incomplete write
			return false;
		} else {
			out.clear();
			return true;
		}
	}

	/**
	 * Tries to write all the pending messages to the channel.
	 *
	 * @return true if all the messages have been written, false otherwise.
	 * @throws IOException
	 */
	public boolean doWrite() throws IOException {
		if (currentBuffer != null && currentBuffer.hasRemaining()) {//a message is being written
			channel.write(currentBuffer);
			if (currentBuffer.hasRemaining()) {//incomplete write
				return false;//retry later
			}
		}
		while (true) {
			Packet message = pendingMessages.poll();
			if (message == null) {//empty queue: all the messages have been written.
				return true;
			}

			message.writeTo(out);
			currentBuffer = out.asPacketBuffer(message.getId());

			channel.write(currentBuffer);
			if (currentBuffer.hasRemaining()) {//incomplete write
				return false;//retry later
			} else {
				out.clear();
				//continue the loop
			}
		}
	}

}
