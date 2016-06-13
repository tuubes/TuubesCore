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
import org.mcphoton.impl.server.Main;
import org.mcphoton.network.Packet;
import org.slf4j.Logger;

/**
 * A PacketWriter writes packets to a SocketChannel. In the minecraft protocol, a packet is a block of data
 * (bytes) sent over the network, preceded by its size (encoded as a VarInt). With a non-blocking
 * SocketChannel's write, the write may be incomplete. The PacketWriter detects this situation and reports it
 * to the caller, while keeping in memory the remaining data to write, so that we can finish writing it later.
 *
 * @author TheElectronWill
 */
public final class PacketWriter {

	private final SocketChannel channel;
	private final Queue<Packet> pendingQueue = new LinkedList<>();
	private final ArrayProtocolOutputStream out = new ArrayProtocolOutputStream();
	private ByteBuffer currentBuffer;
	private final Logger logger = Main.serverInstance.logger;

	public PacketWriter(SocketChannel sc) {
		this.channel = sc;
	}

	/**
	 * Adds a packet to the queue of pending messages, to write it later.
	 */
	public boolean enqueue(Packet packet) {
		return pendingQueue.offer(packet);
	}

	/**
	 * Writes a packet as soon as possible.
	 *
	 * @return true if the message has been completely written immediatly, false otherwise.
	 * @throws IOException
	 */
	public boolean writeASAP(Packet message) throws IOException {
		if (!pendingQueue.isEmpty() || currentBuffer != null && currentBuffer.hasRemaining()) {
			//cannot write now because another message is being written
			enqueue(message);
			return false;
		}

		message.writeTo(out);
		currentBuffer = out.asPacketBuffer(message.getId());
		logger.trace("currentBuffer {}", currentBuffer);

		channel.write(currentBuffer);
		if (currentBuffer.hasRemaining()) {//incomplete write
			return false;
		} else {
			out.clear();
			return true;
		}
	}

	/**
	 * Tries to write all the pending packets to the channel. If this method returns <code>false</code>, then
	 * it should be called again later, when a write operation on the SocketChannel becomes possible again.
	 *
	 * @return true if all the packets have been written, false otherwise.
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
			Packet message = pendingQueue.poll();
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
