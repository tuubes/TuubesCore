package org.mcphoton.impl.network;

import com.electronwill.utils.IndexMap;
import java.nio.ByteBuffer;
import java.util.Collection;
import org.mcphoton.network.Client;
import org.mcphoton.network.ConnectionState;
import org.mcphoton.network.Packet;
import org.mcphoton.network.PacketHandler;
import org.mcphoton.network.PacketsManager;

/**
 * Implementation of the PacketsManager.
 *
 * @author TheElectronWill
 */
public final class PhotonPacketsManager implements PacketsManager {

	//--- ServerBound ---
	private final IndexMap<Class<? extends Packet>> serverInitPackets, serverStatusPackets, serverLoginPackets, serverPlayPackets;
	private final IndexMap<Collection<PacketHandler>> serverInitHandlers, serverStatusHandlers, serverLoginHandlers, serverPlayHandlers;

//--- ClientBound ---
	private final IndexMap<Class<? extends Packet>> clientInitPackets, clientStatusPackets, clientLoginPackets, clientPlayPackets;
	private final IndexMap<Collection<PacketHandler>> clientInitHandlers, clientStatusHandlers, clientLoginHandlers, clientPlayHandlers;

	public PhotonPacketsManager() {
		//TODO set the correct sizes
		this.serverInitPackets = new IndexMap<>();
		this.serverStatusPackets = new IndexMap<>();
		this.serverLoginPackets = new IndexMap<>();
		this.serverPlayPackets = new IndexMap<>();

		this.serverInitHandlers = new IndexMap<>();
		this.serverStatusHandlers = new IndexMap<>();
		this.serverLoginHandlers = new IndexMap<>();
		this.serverPlayHandlers = new IndexMap<>();

		this.clientInitPackets = new IndexMap<>();
		this.clientStatusPackets = new IndexMap<>();
		this.clientLoginPackets = new IndexMap<>();
		this.clientPlayPackets = new IndexMap<>();

		this.clientInitHandlers = new IndexMap<>();
		this.clientStatusHandlers = new IndexMap<>();
		this.clientLoginHandlers = new IndexMap<>();
		this.clientPlayHandlers = new IndexMap<>();
	}

	@Override
	public void registerPacket(ConnectionState state, boolean serverBound, int packetId, Class<? extends Packet> packetClass) {

	}

	@Override
	public void registerHandler(ConnectionState state, boolean serverBound, int packetId, PacketHandler<? extends Packet> handler) {
		; //TODO
	}

	@Override
	public void unregisterPacket(ConnectionState state, boolean serverBound, int packetId) {
		; //TODO
	}

	@Override
	public void unregisterHandler(ConnectionState state, boolean serverBound, int packetId, PacketHandler<? extends Packet> handler) {
		; //TODO
	}

	@Override
	public Class<? extends Packet> getRegisteredPacket(ConnectionState state, boolean serverBound, int packetId) {
		; //TODO
	}

	@Override
	public PacketHandler[] getRegisteredHandlers(ConnectionState state, boolean serverBound, int packetId) {
		; //TODO
	}

	@Override
	public void sendPacket(Packet packet, Client client) {
		; //TODO
	}

	@Override
	public void sendPacket(Packet packet, Client... clients) {
		; //TODO
	}

	@Override
	public void sendPacket(Packet packet, Client client, Runnable onSendingCompleted) {
		; //TODO
	}

	@Override
	public Packet parsePacket(ByteBuffer data, ConnectionState connState, boolean serverBound) {
		; //TODO
	}

	@Override
	public void handle(Packet packet, Client client) {
		; //TODO
	}

}
