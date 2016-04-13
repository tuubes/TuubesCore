package org.mcphoton.impl.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
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
	
	/**
	 * The codecs, in the output order (server -> client).
	 */
	private final List<Codec> codecs = Collections.synchronizedList(new ArrayList<>(2));
	private final InetSocketAddress address;
	private final SocketChannel channel;
	private final MessageReader messageReader;
	private final MessageWriter messageWriter;
	private volatile Optional<Player> player = Optional.empty();
	private volatile int authVerifyToken = -1;
	private volatile ConnectionState state = ConnectionState.INIT;
	
	public PhotonClient(SocketChannel channel) throws IOException {
		this.channel = channel;
		this.messageReader = new MessageReader(channel);
		this.messageWriter = new MessageWriter(channel);
		this.address = (InetSocketAddress) channel.getRemoteAddress();
	}
	
	public PhotonClient(SocketChannel channel, MessageReader messageReader, MessageWriter messageWriter) throws IOException {
		this.channel = channel;
		this.messageReader = messageReader;
		this.messageWriter = messageWriter;
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
	public void setConnectionState(ConnectionState newState) {
		this.state = newState;
	}
	
	public int getAuthVerifyToken() {
		return authVerifyToken;
	}
	
	public void setAuthVerifyToken(int authVerifyToken) {
		this.authVerifyToken = authVerifyToken;
	}
	
	/**
	 * Gets the codecs, in the output/encoding order (server -> client). The returned list is thread-safe.
	 */
	public List<Codec> getCodecs() {
		return codecs;
	}
	
	public MessageReader getMessageReader() {
		return messageReader;
	}
	
	public MessageWriter getMessageWriter() {
		return messageWriter;
	}
	
	/**
	 * Gets the SocketChannel connected to this client.
	 */
	public SocketChannel getChannel() {
		return channel;
	}
	
	/**
	 * Decodes a message with the current codecs.
	 * 
	 * @param data the message's data
	 * @return a ByteBuffer that contains the decoded message's data, ie a varInt for the packet's id followed by the
	 *         packet's data.
	 */
	public ByteBuffer decodeWithCodecs(ByteBuffer data) throws IOException {
		synchronized (codecs) {
			ListIterator<Codec> it = codecs.listIterator(codecs.size());
			try {
				while (it.hasPrevious()) {// loop in reverse order of the list
					Codec codec = it.previous();
					data = codec.decode(data);
				}
			} catch (Exception ex) {
				throw new IOException("An error occured while decoding message", ex);
			}
		}
		return data;
	}
	
	/**
	 * Encodes a message with the current codecs.
	 * 
	 * @param data a ByteBuffer that contains the message's data, ie a varInt for the packet's id followed by the
	 *        packet's data.
	 * @return the encoded message's data
	 */
	public ByteBuffer encodeWithCodecs(ByteBuffer data) throws IOException {
		synchronized (codecs) {
			try {
				for (Codec codec : codecs) {
					data = codec.encode(data);
				}
			} catch (Exception ex) {
				throw new IOException("An error occured while encoding message", ex);
			}
		}
		return data;
	}
	
}
