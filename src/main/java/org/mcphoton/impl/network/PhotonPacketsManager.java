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

import com.electronwill.utils.ConcurrentIndexMap;
import com.electronwill.utils.SimpleBag;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Random;
import org.mcphoton.impl.network.handlers.EncryptionResponseHandler;
import org.mcphoton.impl.network.handlers.HandshakeHandler;
import org.mcphoton.impl.network.handlers.LoginStartHandler;
import org.mcphoton.impl.network.handlers.PingHandler;
import org.mcphoton.impl.network.handlers.RequestHandler;
import org.mcphoton.impl.server.PhotonServer;
import org.mcphoton.network.Client;
import org.mcphoton.network.ConnectionState;
import org.mcphoton.network.Packet;
import org.mcphoton.network.PacketHandler;
import org.mcphoton.network.PacketsManager;
import org.mcphoton.network.ProtocolHelper;

/**
 * Implementation of the PacketsManager.
 *
 * @author TheElectronWill
 */
public final class PhotonPacketsManager implements PacketsManager {

	private final PhotonServer server;
	private final Authenticator authenticator;

	//--- ServerBound ---
	private final ConcurrentIndexMap<Class<? extends Packet>> serverInitPackets, serverStatusPackets, serverLoginPackets, serverPlayPackets;
	private final ConcurrentIndexMap<Collection<PacketHandler>> serverInitHandlers, serverStatusHandlers, serverLoginHandlers, serverPlayHandlers;

	//--- ClientBound ---
	private final ConcurrentIndexMap<Class<? extends Packet>> clientInitPackets, clientStatusPackets, clientLoginPackets, clientPlayPackets;
	private final ConcurrentIndexMap<Collection<PacketHandler>> clientInitHandlers, clientStatusHandlers, clientLoginHandlers, clientPlayHandlers;

	public PhotonPacketsManager(PhotonServer server) {
		this.server = server;
		this.authenticator = new Authenticator(server.keyPair);

		//TODO set the correct sizes
		this.serverInitPackets = new ConcurrentIndexMap<>();
		this.serverStatusPackets = new ConcurrentIndexMap<>();
		this.serverLoginPackets = new ConcurrentIndexMap<>();
		this.serverPlayPackets = new ConcurrentIndexMap<>();

		this.serverInitHandlers = new ConcurrentIndexMap<>();
		this.serverStatusHandlers = new ConcurrentIndexMap<>();
		this.serverLoginHandlers = new ConcurrentIndexMap<>();
		this.serverPlayHandlers = new ConcurrentIndexMap<>();

		this.clientInitPackets = new ConcurrentIndexMap<>();
		this.clientStatusPackets = new ConcurrentIndexMap<>();
		this.clientLoginPackets = new ConcurrentIndexMap<>();
		this.clientPlayPackets = new ConcurrentIndexMap<>();

		this.clientInitHandlers = new ConcurrentIndexMap<>();
		this.clientStatusHandlers = new ConcurrentIndexMap<>();
		this.clientLoginHandlers = new ConcurrentIndexMap<>();
		this.clientPlayHandlers = new ConcurrentIndexMap<>();
	}

	private ConcurrentIndexMap<Class<? extends Packet>> getPacketsMap(ConnectionState state, boolean serverBound) {
		if (serverBound) {
			switch (state) {
				case LOGIN:
					return serverLoginPackets;
				case PLAY:
					return serverPlayPackets;
				case STATUS:
					return serverStatusPackets;
				default:
					return serverInitPackets;
			}
		} else {
			switch (state) {
				case LOGIN:
					return clientLoginPackets;
				case PLAY:
					return clientPlayPackets;
				case STATUS:
					return clientStatusPackets;
				default:
					return clientInitPackets;
			}
		}
	}

	private ConcurrentIndexMap<Collection<PacketHandler>> getHandlersMap(ConnectionState state, boolean serverBound) {
		if (serverBound) {
			switch (state) {
				case LOGIN:
					return serverLoginHandlers;
				case PLAY:
					return serverPlayHandlers;
				case STATUS:
					return serverStatusHandlers;
				default:
					return serverInitHandlers;
			}
		} else {
			switch (state) {
				case LOGIN:
					return clientLoginHandlers;
				case PLAY:
					return clientPlayHandlers;
				case STATUS:
					return clientStatusHandlers;
				default:
					return clientInitHandlers;
			}
		}
	}

	@Override
	public void registerPacket(ConnectionState state, boolean serverBound, int packetId, Class<? extends Packet> packetClass) {
		getPacketsMap(state, serverBound).put(packetId, packetClass);
	}

	@Override
	public void registerHandler(ConnectionState state, boolean serverBound, int packetId, PacketHandler<? extends Packet> handler) {
		ConcurrentIndexMap<Collection<PacketHandler>> map = getHandlersMap(state, serverBound);
		Collection<PacketHandler> handlers = map.computeIfAbsent(packetId, (key) -> new SimpleBag<>());
		synchronized (handlers) {
			handlers.add(handler);
		}
	}

	@Override
	public void unregisterPacket(ConnectionState state, boolean serverBound, int packetId) {
		getPacketsMap(state, serverBound).remove(packetId);
	}

	@Override
	public void unregisterHandler(ConnectionState state, boolean serverBound, int packetId, PacketHandler<? extends Packet> handler) {
		Collection<PacketHandler> handlers = getHandlersMap(state, serverBound).get(packetId);
		if (handlers != null) {
			synchronized (handlers) {
				handlers.remove(handler);
			}
		}
	}

	@Override
	public Class<? extends Packet> getRegisteredPacket(ConnectionState state, boolean serverBound, int packetId) {
		return getPacketsMap(state, serverBound).get(packetId);
	}

	@Override
	public Collection<PacketHandler> getRegisteredHandlers(ConnectionState state, boolean serverBound, int packetId) {
		return getHandlersMap(state, serverBound).get(packetId);
	}

	@Override
	public void sendPacket(Packet packet, Client client) {
		server.logger.debug("Send packet {} to the client {}", packet, client);
		server.networkThread.outboundQueue().add(new PacketSending(packet, (PhotonClient) client));
	}

	@Override
	public void sendPacket(Packet packet, Client... clients) {
		server.logger.debug("Send packet {} to multiple clients: {}", packet, clients);
		for (Client client : clients) {
			server.networkThread.outboundQueue().add(new PacketSending(packet, (PhotonClient) client));
		}
	}

	@Override
	public void sendPacket(Packet packet, Client client, Runnable completionAction) {
		server.logger.debug("Send packet {} to the client {} with a completion action {}", packet, client, completionAction);
		server.networkThread.outboundQueue().add(new PacketSending(packet, (PhotonClient) client, completionAction));
	}

	@Override
	public Packet parsePacket(ByteBuffer data, ConnectionState connState, boolean serverBound) {
		server.logger.debug("parse packet: {}, state: {}, serverBound: {}", data, connState, serverBound);
		int packetId = ProtocolHelper.readVarInt(data);
		try {
			Class<? extends Packet> packetClass = getRegisteredPacket(connState, serverBound, packetId);
			Packet packet = packetClass.newInstance();
			return packet.readFrom(data);
		} catch (Exception ex) {
			server.logger.error("Cannot create packet object with id {}", packetId, ex);
		}
		return null;
	}

	@Override
	public void handle(Packet packet, Client client) {
		server.logger.debug("Handling packet {} from {}", packet.toString(), client.getAddress().toString());
		ConcurrentIndexMap<Collection<PacketHandler>> handlersMap = getHandlersMap(client.getConnectionState(), packet.isServerBound());
		Collection<PacketHandler> handlers = handlersMap.get(packet.getId());
		if (handlers != null) {
			for (PacketHandler handler : handlers) {
				handler.handle(packet, client);
			}
		}
	}

	public void registerGamePackets() {
		serverInitPackets.put(0, org.mcphoton.network.handshaking.serverbound.HandshakePacket.class);

		serverStatusPackets.put(0, org.mcphoton.network.status.serverbound.RequestPacket.class);
		serverStatusPackets.put(1, org.mcphoton.network.status.serverbound.PingPacket.class);

		serverLoginPackets.put(0, org.mcphoton.network.login.serverbound.LoginStartPacket.class);
		serverLoginPackets.put(1, org.mcphoton.network.login.serverbound.EncryptionResponsePacket.class);

		serverPlayPackets.put(0x03, org.mcphoton.network.play.serverbound.ClientStatusPacket.class);

		clientStatusPackets.put(0, org.mcphoton.network.status.clientbound.ResponsePacket.class);
		clientStatusPackets.put(1, org.mcphoton.network.status.clientbound.PongPacket.class);

		clientLoginPackets.put(0, org.mcphoton.network.login.clientbound.DisconnectPacket.class);
		clientLoginPackets.put(1, org.mcphoton.network.login.clientbound.EncryptionRequestPacket.class);
		clientLoginPackets.put(2, org.mcphoton.network.login.clientbound.LoginSuccessPacket.class);
		clientLoginPackets.put(3, org.mcphoton.network.login.clientbound.SetCompressionPacket.class);

		clientPlayPackets.put(0x20, org.mcphoton.network.play.clientbound.ChunkDataPacket.class);
		clientPlayPackets.put(0x23, org.mcphoton.network.play.clientbound.JoinGamePacket.class);
		clientPlayPackets.put(0x2E, org.mcphoton.network.play.clientbound.PlayerPositionAndLookPacket.class);
		clientPlayPackets.put(0x18, org.mcphoton.network.play.clientbound.PluginMessagePacket.class);
		clientPlayPackets.put(0x0D, org.mcphoton.network.play.clientbound.ServerDifficultyPacket.class);
		clientPlayPackets.put(0x43, org.mcphoton.network.play.clientbound.SpawnPositionPacket.class);
	}

	public void registerPacketHandlers() {
		serverInitHandlers.put(0, new SimpleBag(new HandshakeHandler()));

		serverStatusHandlers.put(0, new SimpleBag(new RequestHandler(server)));
		serverStatusHandlers.put(1, new SimpleBag(new PingHandler(this)));

		serverLoginHandlers.put(0, new SimpleBag(new LoginStartHandler(new Random(), authenticator, this)));
		serverLoginHandlers.put(1, new SimpleBag(new EncryptionResponseHandler(authenticator, this)));
	}

}
