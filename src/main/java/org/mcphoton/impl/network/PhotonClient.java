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
import org.mcphoton.entity.living.player.Player;
import org.mcphoton.network.Client;
import org.mcphoton.network.ConnectionState;

/**
 * Implementation of the Client interface.
 *
 * @author TheElectronWill
 *
 */
public final class PhotonClient implements Client {

	final InetSocketAddress address;
	final Codec[] codecs;//the codecs, in the output order (server -> client).
	final SocketChannel channel;
	final MessageReader messageReader;
	final MessageWriter messageWriter;
	volatile Optional<Player> player = Optional.empty();
	volatile int authVerifyToken = -1;
	volatile ConnectionState state = ConnectionState.INIT;

	public PhotonClient(SocketChannel channel, Codec[] codecs) throws IOException {
		this.channel = channel;
		this.codecs = codecs;
		this.messageReader = new MessageReader(channel, 256, 512);
		this.messageWriter = new MessageWriter(channel);
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

}
