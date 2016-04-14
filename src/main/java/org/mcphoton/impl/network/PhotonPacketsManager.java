package org.mcphoton.impl.network;

import com.electronwill.utils.IndexMap;
import com.electronwill.utils.SimpleBag;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.mcphoton.impl.PhotonServer;
import org.mcphoton.network.Client;
import org.mcphoton.network.ConnectionState;
import org.mcphoton.network.Packet;
import org.mcphoton.network.PacketHandler;
import org.mcphoton.network.PacketsManager;
import org.mcphoton.network.ProtocolHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the PacketsManager.
 *
 * @author TheElectronWill
 */
public final class PhotonPacketsManager implements PacketsManager {

	private static final Logger logger = LoggerFactory.getLogger("PhotonPacketsManager");
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
		List<PhotonClient> clientList = Collections.singletonList((PhotonClient) client);
		server.networkOutputThread.enqueue(new PacketSending(packet, clientList));
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
		int packetId = ProtocolHelper.readVarInt(data);
		try {
			Class<? extends Packet> packetClass = getRegisteredPacket(connState, serverBound, 0);
			Packet packet = packetClass.newInstance();
			return packet.readFrom(data);
		} catch (InstantiationException | IllegalAccessException | NullPointerException ex) {
			logger.error("Cannot create packet object with id {}", packetId, ex);
		}
		return null;
	}

	@Override
	public void handle(Packet packet, Client client) {
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

}
