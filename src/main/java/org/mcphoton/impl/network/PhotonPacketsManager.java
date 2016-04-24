package org.mcphoton.impl.network;

import com.electronwill.utils.IndexMap;
import com.electronwill.utils.SimpleBag;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.mcphoton.Photon;
import org.mcphoton.impl.server.PhotonServer;
import org.mcphoton.network.Client;
import org.mcphoton.network.ConnectionState;
import org.mcphoton.network.Packet;
import org.mcphoton.network.PacketHandler;
import org.mcphoton.network.PacketsManager;
import org.mcphoton.network.ProtocolHelper;
import org.mcphoton.network.handshaking.serverbound.HandshakePacket;
import org.mcphoton.network.status.clientbound.PongPacket;
import org.mcphoton.network.status.clientbound.ResponsePacket;
import org.mcphoton.network.status.serverbound.PingPacket;
import org.mcphoton.network.status.serverbound.RequestPacket;

/**
 * Implementation of the PacketsManager.
 *
 * @author TheElectronWill
 */
public final class PhotonPacketsManager implements PacketsManager {

	private final PhotonServer server;

	//--- ServerBound ---
	private final IndexMap<Class<? extends Packet>> serverInitPackets, serverStatusPackets, serverLoginPackets, serverPlayPackets;
	private final IndexMap<Collection<PacketHandler>> serverInitHandlers, serverStatusHandlers, serverLoginHandlers, serverPlayHandlers;

	//--- ClientBound ---
	private final IndexMap<Class<? extends Packet>> clientInitPackets, clientStatusPackets, clientLoginPackets, clientPlayPackets;
	private final IndexMap<Collection<PacketHandler>> clientInitHandlers, clientStatusHandlers, clientLoginHandlers, clientPlayHandlers;

	public PhotonPacketsManager(PhotonServer server) {
		this.server = server;

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

	private IndexMap<Class<? extends Packet>> getPacketsMap(ConnectionState state, boolean serverBound) {
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

	private IndexMap<Collection<PacketHandler>> getHandlersMap(ConnectionState state, boolean serverBound) {
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
		IndexMap<Class<? extends Packet>> map = getPacketsMap(state, serverBound);
		synchronized (map) {
			map.put(packetId, packetClass);
		}
	}

	@Override
	public void registerHandler(ConnectionState state, boolean serverBound, int packetId, PacketHandler<? extends Packet> handler) {
		IndexMap<Collection<PacketHandler>> map = getHandlersMap(state, serverBound);
		synchronized (map) {
			Collection<PacketHandler> handlers = map.get(packetId);
			if (handlers == null) {
				handlers = new SimpleBag<>();
				map.put(packetId, handlers);
			}
			handlers.add(handler);
		}
	}

	@Override
	public void unregisterPacket(ConnectionState state, boolean serverBound, int packetId) {
		IndexMap<Class<? extends Packet>> map = getPacketsMap(state, serverBound);
		synchronized (map) {
			map.remove(packetId);
		}
	}

	@Override
	public void unregisterHandler(ConnectionState state, boolean serverBound, int packetId, PacketHandler<? extends Packet> handler) {
		IndexMap<Collection<PacketHandler>> map = getHandlersMap(state, serverBound);
		synchronized (map) {
			Collection<PacketHandler> handlers = map.get(packetId);
			if (handlers != null) {
				handlers.remove(handler);
			}
		}
	}

	@Override
	public Class<? extends Packet> getRegisteredPacket(ConnectionState state, boolean serverBound, int packetId) {
		IndexMap<Class<? extends Packet>> map = getPacketsMap(state, serverBound);
		synchronized (map) {
			return map.get(packetId);
		}
	}

	@Override
	public Collection<PacketHandler> getRegisteredHandlers(ConnectionState state, boolean serverBound, int packetId) {
		IndexMap<Collection<PacketHandler>> map = getHandlersMap(state, serverBound);
		synchronized (map) {
			return map.get(packetId);
		}
	}

	@Override
	public void sendPacket(Packet packet, Client client) {
		server.networkOutputThread.enqueue(new PacketSending(packet, (PhotonClient) client));
	}

	@Override
	public void sendPacket(Packet packet, Client... clients) {
		List clientList = Arrays.asList(clients);
		server.networkOutputThread.enqueue(new PacketSending(packet, clientList));
	}

	@Override
	public void sendPacket(Packet packet, Client client, Runnable onSendingCompleted) {
		//TODO
		throw new UnsupportedOperationException("Not yet implemented, sorry");
	}

	@Override
	public Packet parsePacket(ByteBuffer data, ConnectionState connState, boolean serverBound) {
		server.logger.debug("parse packet: {}, state: {}, serverBound: {}" + serverBound, data, connState, serverBound);
		int packetId = ProtocolHelper.readVarInt(data);
		try {
			Class<? extends Packet> packetClass = getRegisteredPacket(connState, serverBound, 0);
			Packet packet = packetClass.newInstance();
			return packet.readFrom(data);
		} catch (InstantiationException | IllegalAccessException | NullPointerException ex) {
			server.logger.error("Cannot create packet object with id {}", packetId, ex);
		}
		return null;
	}

	@Override
	public void handle(Packet packet, Client client) {
		server.logger.debug("Handling packet {} from {}", packet.toString(), client.getAddress().toString());
		IndexMap<Collection<PacketHandler>> map = getHandlersMap(client.getConnectionState(), packet.isServerBound());
		synchronized (map) {
			Collection<PacketHandler> handlers = map.get(packet.getId());
			if (handlers != null) {
				for (PacketHandler handler : handlers) {
					handler.handle(packet, client);
				}
			}
		}
	}

	public void registerGamePackets() {
		synchronized (serverInitPackets) {
			serverInitPackets.put(0, org.mcphoton.network.handshaking.serverbound.HandshakePacket.class);
		}
		synchronized (serverStatusPackets) {
			serverStatusPackets.put(0, org.mcphoton.network.status.serverbound.RequestPacket.class);
			serverStatusPackets.put(1, org.mcphoton.network.status.serverbound.PingPacket.class);
		}
		synchronized (serverLoginPackets) {
			serverLoginPackets.put(0, org.mcphoton.network.login.serverbound.LoginStartPacket.class);
			serverLoginPackets.put(1, org.mcphoton.network.login.serverbound.EncryptionResponsePacket.class);
		}
		/* TODO
		 * synchronized(serverPlayPackets){
		 *
		 * }
		 */
		synchronized (clientStatusPackets) {
			clientStatusPackets.put(0, org.mcphoton.network.status.clientbound.ResponsePacket.class);
			clientStatusPackets.put(1, org.mcphoton.network.status.clientbound.PongPacket.class);
		}
		synchronized (clientLoginPackets) {
			clientLoginPackets.put(0, org.mcphoton.network.login.clientbound.DisconnectPacket.class);
			clientLoginPackets.put(1, org.mcphoton.network.login.clientbound.EncryptionRequestPacket.class);
			clientLoginPackets.put(2, org.mcphoton.network.login.clientbound.LoginSuccessPacket.class);
			clientLoginPackets.put(3, org.mcphoton.network.login.clientbound.SetCompressionPacket.class);
		}
		synchronized (clientPlayPackets) {
			clientPlayPackets.put(0x20, org.mcphoton.network.play.clientbound.ChunkDataPacket.class);
			clientPlayPackets.put(0x03, org.mcphoton.network.play.clientbound.ClientStatusPacket.class);
			clientPlayPackets.put(0x23, org.mcphoton.network.play.clientbound.JoinGamePacket.class);
			clientPlayPackets.put(0x2E, org.mcphoton.network.play.clientbound.PlayerPositionAndLookPacket.class);
			clientPlayPackets.put(0x18, org.mcphoton.network.play.clientbound.PluginMessagePacket.class);
			clientPlayPackets.put(0x0D, org.mcphoton.network.play.clientbound.ServerDifficultyPacket.class);
			clientPlayPackets.put(0x43, org.mcphoton.network.play.clientbound.SpawnPositionPacket.class);
		}

	}

	public void registerPacketHandlers() {
		registerHandler(ConnectionState.INIT, true, 0, (HandshakePacket packet, Client client) -> {
			server.logger.debug("Set client state to " + packet.nextState);
			if (packet.nextState == 1) {
				client.setConnectionState(ConnectionState.STATUS);
			} else if (packet.nextState == 2) {
				client.setConnectionState(ConnectionState.LOGIN);
			} else {
				//invalid
			}
		});
		registerHandler(ConnectionState.STATUS, true, 0, (RequestPacket packet, Client client) -> {
			ResponsePacket response = new ResponsePacket();
			StringBuilder jsonBuilder = new StringBuilder("{");
			jsonBuilder.append("\"version\":{");
			jsonBuilder.append("\"name\":\"").append(Photon.getMinecraftVersion()).append("\",");
			jsonBuilder.append("\"protocol\":").append(HandshakePacket.CURRENT_PROTOCOL_VERSION);
			jsonBuilder.append("},");
			jsonBuilder.append("\"players\":{");
			jsonBuilder.append("\"max\":").append(server.maxPlayers).append(',');
			jsonBuilder.append("\"online\":").append(server.onlinePlayers.size());
			jsonBuilder.append("},");
			jsonBuilder.append("\"description\":{");
			jsonBuilder.append("\"text\":\"").append(server.motd).append("\"");
			jsonBuilder.append("}}");
			response.jsonResponse = jsonBuilder.toString();
			server.logger.debug("Sending ResponsePacket to the client: {}", jsonBuilder);
			sendPacket(response, client);
		});
		registerHandler(ConnectionState.STATUS, true, 1, (PingPacket packet, Client client) -> {
			PongPacket pong = new PongPacket();
			pong.payload = System.currentTimeMillis();
			sendPacket(pong, client);
		});
	}

}
