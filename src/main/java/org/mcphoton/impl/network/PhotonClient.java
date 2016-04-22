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
