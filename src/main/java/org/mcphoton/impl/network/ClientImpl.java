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
import java.nio.channels.SocketChannel;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import org.mcphoton.entity.living.Player;
import org.mcphoton.network.Client;
import org.mcphoton.network.ConnectionState;

/**
 * Implementation of the Client interface.
 * <h2>How codecs work</h2>
 * <p>
 * Codecs modify the content of a ByteBuffer. There are 2 codecs associated with a client: a compression
 * codec and a cipher codec. The compression codec is used just before parsing the packet (input) or just
 * after serializing it (output). The cipher codec is used just after a channel's read (input) or just before
 * a channel's write (output). See the schema below: <br />
 * <li>Packet input (client to server): SocketChannel -> CipherCodec -> PacketReader -> CompressionCodec ->
 * Packet object</li>
 * <li>Packet output (server to client): Packet object -> PacketWriter and CompressionCodec-> CipherCodec ->
 * SocketChannel</li>
 * </p>
 *
 * @author TheElectronWill
 *
 */
public final class ClientImpl implements Client {

	final InetSocketAddress address;
	final SocketChannel channel;
	final PacketReader packetReader;
	final PacketWriter packetWriter;
	volatile Optional<Player> player = Optional.empty();
	volatile int authVerifyToken = -1;
	volatile ConnectionState state = ConnectionState.INIT;
	volatile Codec cipherCodec, compressionCodec;
	volatile boolean closed = false;
	volatile ScheduledFuture<?> keepClientRunnable;

	public ClientImpl(SocketChannel channel) throws IOException {
		this(channel, new NoCodec(), new NoCodec());
	}

	public ClientImpl(SocketChannel channel, Codec cipherCodec, Codec compressionCodec) throws IOException {
		this.channel = channel;
		this.cipherCodec = cipherCodec;
		this.compressionCodec = compressionCodec;
		this.packetReader = new PacketReader(channel, 128, 1024);
		this.packetWriter = new PacketWriter(channel);
		this.address = (InetSocketAddress) channel.getRemoteAddress();
	}

	@Override
	public InetSocketAddress getAddress() {
		return address;
	}

	@Override
	public Optional<Player> getPlayer() {
		return player;
	}

	@Override
	public ConnectionState getConnectionState() {
		return state;
	}

	@Override
	public void setConnectionState(ConnectionState state) {
		this.state = state;
	}

	@Override
	public void closeConnection() throws IOException {
		channel.close();
		closed = true;
	}

	@Override
	public boolean isClosed() {
		return closed;
	}
	
	@Override
	public boolean isLocal() {
		return address.getAddress().getHostAddress().equals("127.0.0.1");
	}
	
	@Override
	public String toString() {
		return "PhotonClient{" + "address=" + address + ", player=" + player + ", state=" + state + '}';
	}

	/**
	 * Enable encrypted communication between the client and the server. This method must be called in the
	 * network thread.
	 *
	 * @param cipherCodec the codec to use.
	 */
	public void enableEncryption(AESCodec cipherCodec) {
		this.cipherCodec = cipherCodec;
		packetWriter.setCipherCodec(cipherCodec);
		packetReader.setCipherCodec(cipherCodec);
	}

	public void setPlayer(Player player) {
		this.player = Optional.of(player);
	}

	public void setKeepClientRunnable(ScheduledFuture<?> futur) {
		keepClientRunnable = futur;
	}
	
	
	@Override
	public ScheduledFuture<?> getKeepClientRunnable() {
		return keepClientRunnable;
	}
}
