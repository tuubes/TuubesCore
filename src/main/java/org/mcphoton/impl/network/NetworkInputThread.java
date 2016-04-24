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
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import org.mcphoton.Photon;
import org.mcphoton.impl.server.Main;
import org.mcphoton.impl.server.PhotonServer;
import org.mcphoton.network.Packet;
import org.mcphoton.network.PacketsManager;

/**
 * The thread that manages all the network input. It accepts new client connections and reads incoming
 * packets.
 *
 * @author TheElectronWill
 *
 */
public final class NetworkInputThread extends Thread {

	private volatile boolean run = true;
	private final InetSocketAddress serverAddress;
	private final Selector selector;
	private final ServerSocketChannel ssc;

	public NetworkInputThread(InetSocketAddress serverAddress) throws IOException {
		super("network-input");
		this.serverAddress = serverAddress;
		ssc = ServerSocketChannel.open();
		selector = Selector.open();
	}

	/**
	 * Stops this Thread nicely, as soon as possible but without any forcing.
	 */
	public void stopNicely() {
		run = false;
	}

	@Override
	public void run() {
		try {
			ssc.bind(serverAddress);
			ssc.configureBlocking(false);
			ssc.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(10);
		}
		final PacketsManager packetsManager = Photon.getPacketsManager();
		final PhotonServer server = Main.serverInstance;
		while (run) {
			try {
				synchronized (this) {
				} // to support selector.wakeUp (see NetworkOutputThread.java)
				int selectedCount = selector.select();
				server.logger.trace("The Selector selected {} key(s)", selectedCount);
				if (selectedCount == 0) {
					continue;
				}
				Iterator<SelectionKey> it = selector.selectedKeys().iterator();
				while (it.hasNext()) {
					SelectionKey key = it.next();
					server.logger.trace("Selected key {} with ops {}", key, key.readyOps());
					try {
						if (key.isAcceptable()) {
							SocketChannel channel = ssc.accept();
							channel.configureBlocking(false);
							PhotonClient client = new PhotonClient(channel, new Codec[] {new NoCodec()});
							channel.register(selector, SelectionKey.OP_READ, client);
						} else if (key.isReadable()) {
							PhotonClient client = (PhotonClient) key.attachment();
							MessageReader messageReader = client.messageReader;
							ByteBuffer messageData;
							while ((messageData = messageReader.readNext()) != null) {
								server.logger.trace("messageData {}", messageData);
								for (int i = client.codecs.length - 1; i >= 0; i--) {
									messageData = client.codecs[i].decode(messageData);
									Packet packet = packetsManager.parsePacket(messageData, client.getConnectionState(), true);
									packetsManager.handle(packet, client);
								}
							}
							if (messageReader.hasReachedEndOfStream()) {// client disconnected
								server.logger.debug("END OF STREAM");
								key.cancel();
								client.getPlayer().ifPresent(server.onlinePlayers::remove);
							}
						} else if (key.isWritable()) {
							PhotonClient client = (PhotonClient) key.attachment();
							boolean writeComplete = client.messageWriter.doWrite();
							if (writeComplete) {
								key.cancel();
							}
						}
					} catch (Exception e) {
						server.logger.error("Error while handling a java nio SelectionKey", e);
					} finally {
						it.remove();// don't process the same key more than once!
					}
				}
			} catch (IOException e) {
				server.logger.error("Severe IO error in the selector loop", e);
			}
		}
		try {
			selector.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Selector getSelector() {
		return selector;
	}

}
