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
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.channels.SelectionKey.OP_WRITE;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.mcphoton.impl.server.Main;
import org.mcphoton.impl.server.PhotonServer;
import org.mcphoton.network.Packet;

/**
 *
 * @author TheElectronWill
 */
public class NioNetworkThread extends Thread {

	private final PhotonServer server;
	private final InetSocketAddress address;
	private final Selector selector;
	private final ServerSocketChannel ssc;
	private final BlockingQueue<PacketSending> outboundPacketsQueue = new ArrayBlockingQueue<>(500);//fixed size
	private final List<PacketSending> packetsProcessingList = new ArrayList<>(50);
	private volatile boolean run = true;

	public NioNetworkThread(InetSocketAddress address, PhotonServer server) throws IOException {
		super("nio-network");
		this.selector = Selector.open();
		this.ssc = ServerSocketChannel.open();
		this.address = address;
		this.server = server;
	}

	/**
	 * Stops this Thread nicely, as soon as possible but without any forcing.
	 */
	public void stopNicely() {
		run = false;
	}

	private void launch() {
		try {
			ssc.bind(address);
			ssc.configureBlocking(false);
			ssc.register(selector, OP_ACCEPT);
		} catch (IOException ex) {
			Main.SERVER.logger.error("Unable to launch the ServerSocketChannel.", ex);
			System.exit(10);//Photon cannot work with this error
		}
	}

	private void shutdown() {
		try {
			selector.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void run() {
		launch();
		while (run) {
			int selected;
			try {
				selected = selectAndProcess();
			} catch (IOException ex) {
				server.logger.error("Unable to select keys for processing", ex);
				server.logger.error("Photon cannot work with such an error. The network thread will shut down now.");
				run = false;
				return;
			}
			int written = writeOutboundPackets();
			if (selected == 0 && written == 0) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException ex) {
					//ignore
				}
			}
		}
		shutdown();
	}

	/**
	 * Calls <code>selector.selectNow()</code> and processes the selected keys.
	 *
	 * @return the number of selected keys.
	 * @throws IOException if an error occurs during <code>selector.selectNow()</code>.
	 */
	private int selectAndProcess() throws IOException {
		int selected = selector.selectNow();
		if (selected == 0) {
			return selected;
		}
		server.logger.trace("Selected {} keys with the Selector.", selected);
		Set<SelectionKey> keys = selector.selectedKeys();
		for (SelectionKey key : keys) {
			if (key.isAcceptable()) {
				acceptClient(key);
			}
			if (key.isReadable()) {
				readData(key);
			}
			if (key.isValid() && key.isWritable()) {
				continuePendingWrite(key);
			}
		}
		keys.clear();
		return selected;
	}

	/**
	 * Accepts an incoming connection.
	 */
	private void acceptClient(SelectionKey key) {
		try {
			SocketChannel channel = ssc.accept();
			channel.configureBlocking(false);
			PhotonClient client = new PhotonClient(channel);
			channel.register(selector, OP_READ, client);
		} catch (Exception ex) {
			server.logger.error("Unable to accept the client", ex);
		}
	}

	/**
	 * Reads some data from the key's channel.
	 */
	private void readData(SelectionKey key) {
		PhotonClient client = (PhotonClient) key.attachment();
		try {
			while (true) {
				ByteBuffer packetData = client.packetReader.readNext();
				if (packetData == PacketReader.END_OF_STREAM) {//client disconnected
					server.logger.debug("END OF STREAM for {}", client.address);
					key.cancel();
					client.getPlayer().ifPresent(server.onlinePlayers::remove);
					return;
				} else if (packetData == null) {//not enough bytes available to read the packet
					return;
				} else {//read operation succeed
					Packet packet = server.packetsManager.parsePacket(packetData, client.state, true);
					server.packetsManager.handle(packet, client);
				}
			}
		} catch (Exception ex) {
			server.logger.error("Unable to read data from client {}", client, ex);
		}
	}

	/**
	 * Continues the pending write operation on a key's channel.
	 */
	private void continuePendingWrite(SelectionKey key) {
		PhotonClient client = (PhotonClient) key.attachment();
		try {
			boolean writeComplete = client.packetWriter.doWrite();
			if (writeComplete) {
				key.interestOps(OP_READ);//remove OP_WRITE
			}
		} catch (Exception ex) {
			server.logger.error("Unable to continue the pending writes for client {}", client, ex);
		}
	}

	/**
	 * Take at most 50 elements from the outbound packets queue, and try to write the packets.
	 *
	 * @return the number of packets taken from the queue.
	 */
	private int writeOutboundPackets() {
		int drained = outboundPacketsQueue.drainTo(packetsProcessingList, 50);
		if (drained == 0) {
			return drained;
		}
		server.logger.trace("Drained {} PacketSendings from the queue.", drained);
		for (PacketSending sending : packetsProcessingList) {
			try {
				PhotonClient recipient = sending.recipient;
				boolean complete = recipient.packetWriter.writeASAP(sending);
				if (!complete) {
					server.logger.trace("Incomplete write -> put into the queue");
					recipient.channel.register(selector, OP_READ | OP_WRITE, recipient);
				}
			} catch (IOException ex) {
				server.logger.error("Unable to write outbound packet. Infos: {}", sending, ex);
			}
		}
		packetsProcessingList.clear();
		return drained;
	}

	public BlockingQueue<PacketSending> outboundQueue() {
		return outboundPacketsQueue;
	}

}
