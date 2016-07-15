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
import org.mcphoton.network.ProtocolOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.PhotonLogger;

/**
 * A PacketWriter writes packets to a SocketChannel. In the minecraft protocol, a packet is a block of data
 * (bytes) sent over the network, preceded by its size (encoded as a VarInt). With a non-blocking
 * SocketChannel's write, the write may be incomplete. The PacketWriter detects this situation and reports it
 * to the caller, while keeping in memory the remaining data to write, so that we can finish writing it later.
 * <p>
 * This class isn't thread-safe. A client's PacketWriter should only be used in the NIO network thread.
 * </p>
 *
 * @author TheElectronWill
 */
public final class PacketWriter {

	private static final Logger logger = LoggerFactory.getLogger("PacketWriter");

	private final SocketChannel channel;
	private final Queue<PacketSending> pendingQueue = new LinkedList<>();
	private final ArrayProtocolOutputStream out = new ArrayProtocolOutputStream();
	private Codec cipherCodec;
	private ByteBuffer currentBuffer;
	private Runnable pendingCompletionAction;

	public PacketWriter(SocketChannel channel) {
		this(channel, new NoCodec());
	}

	public PacketWriter(SocketChannel channel, Codec cipherCodec) {
		this.channel = channel;
		this.cipherCodec = cipherCodec;
		((PhotonLogger) logger).setLevel(Main.SERVER.logger.getLevel());
	}

	/**
	 * Adds a packet to the queue of pending packets, to write it later.
	 *
	 * @return true if the operation succeed.
	 */
	public boolean enqueue(PacketSending packetSending) {
		return pendingQueue.offer(packetSending);
	}

	/**
	 * Sets the cipher codec for every packet enqueued from now on.
	 *
	 * @return true if the operation succeed.
	 */
	public boolean setCipherCodec(Codec newCipherCodec) {
		return pendingQueue.offer(new PacketSending(new SetCipherCodec(newCipherCodec), null));
	}

	/**
	 * Writes a packet as soon as possible. If this method returns <code>false</code>, the SocketChannel
	 * should be registered with the Selector for OP_WRITE operation, so that the write operation continue as
	 * soon as possible.
	 *
	 * @return true if the packet has been completely written, false otherwise.
	 */
	public boolean writeASAP(PacketSending packetSending) throws IOException {
		logger.trace("--------------------{");
		logger.trace("packet to write: {}", packetSending);
		logger.trace("currentBuffer: {}", currentBuffer);
		logger.trace("output stream: {}", out);
		logger.trace("pending queue: {}", pendingQueue);
		if (!pendingQueue.isEmpty() || currentBuffer != null) {
			//cannot write now because an other packet must be written before
			enqueue(packetSending);
			return false;
		}

		writePacket(packetSending.packet);
		logger.trace("}--------------------");
		if (currentBuffer.hasRemaining()) {//incomplete write
			pendingCompletionAction = packetSending.completionAction;
			return false;
		} else {
			currentBuffer = null;
			packetSending.completionAction.run();
			return true;
		}
	}

	/**
	 * Tries to write all the pending packets to the channel. If this method returns <code>false</code>, then
	 * it should be called again later, when a write operation on the SocketChannel becomes possible again.
	 *
	 * @return true if all the packets have been written, false otherwise.
	 */
	public boolean doWrite() throws IOException {
		if (currentBuffer != null) {//write pending
			channel.write(currentBuffer);//write more data
			if (currentBuffer.hasRemaining()) {//incomplete write
				return false;//retry later
			}
			currentBuffer = null;
			pendingCompletionAction.run();
			pendingCompletionAction = null;
		}
		while (true) {
			PacketSending packetSending = pendingQueue.poll();
			if (packetSending == null) {//empty queue: all the packets have been written.
				return true;
			}
			Packet packet = packetSending.packet;
			if (packet instanceof SetCipherCodec) {//not a real packet, set the cipher codec
				SetCipherCodec setCipherCodec = (SetCipherCodec) packet;
				this.cipherCodec = setCipherCodec.newCipherCodec;
				continue;
			}
			writePacket(packet);
			if (currentBuffer.hasRemaining()) {//incomplete write
				pendingCompletionAction = packetSending.completionAction;
				return false;
			} else {
				currentBuffer = null;
				packetSending.completionAction.run();
				pendingCompletionAction = null;
			}
		}
	}

	/**
	 * Writes the packet's data to {@link #out}, constructs a ByteBuffer with this data, encrypts it with the
	 * cipher codec, and writes it to the socket channel.
	 *
	 * @param packet the packet to write.
	 * @throws IOException
	 */
	private void writePacket(Packet packet) throws IOException {
		packet.writeTo(out);
		currentBuffer = out.constructPacketBuffer(packet.getId());
		out.reset();//resets the position without discarding the data bytes
		try {
			currentBuffer = cipherCodec.encode(currentBuffer);
		} catch (Exception ex) {
			logger.error("Unable to encrypt outbound packet {}", packet);
			throw new IOException("Encryption error while writing outbound packet", ex);
		}
		logger.trace("currentBuffer before write: {}", currentBuffer);
		channel.write(currentBuffer);
		logger.trace("currentBuffer after write: {}", currentBuffer);
	}

	private class SetCipherCodec implements Packet {

		private final Codec newCipherCodec;

		public SetCipherCodec(Codec newCipherCodec) {
			this.newCipherCodec = newCipherCodec;
		}

		@Override
		public int getId() {
			return -1;
		}

		@Override
		public boolean isServerBound() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void writeTo(ProtocolOutputStream out) throws IOException {
			throw new UnsupportedOperationException();
		}

		@Override
		public Packet readFrom(ByteBuffer buff) throws IOException {
			throw new UnsupportedOperationException();
		}

	}

}
