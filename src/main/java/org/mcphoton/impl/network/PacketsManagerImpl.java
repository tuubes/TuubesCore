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
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.Collection;
import java.util.Random;
import org.mcphoton.impl.network.handlers.EncryptionResponseHandler;
import org.mcphoton.impl.network.handlers.HandshakeHandler;
import org.mcphoton.impl.network.handlers.LoginStartHandler;
import org.mcphoton.impl.network.handlers.PingHandler;
import org.mcphoton.impl.network.handlers.RequestHandler;
import static org.mcphoton.impl.server.Main.SERVER;
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
public final class PacketsManagerImpl implements PacketsManager {

	private static final Logger log = LoggerFactory.getLogger(PacketsManagerImpl.class);
	private final Authenticator authenticator;

	//--- ServerBound ---
	private final ConcurrentIndexMap<Class<? extends Packet>> serverInitPackets, serverStatusPackets, serverLoginPackets, serverPlayPackets;
	private final ConcurrentIndexMap<Collection<PacketHandler>> serverInitHandlers, serverStatusHandlers, serverLoginHandlers, serverPlayHandlers;

	//--- ClientBound ---
	private final ConcurrentIndexMap<Class<? extends Packet>> clientInitPackets, clientStatusPackets, clientLoginPackets, clientPlayPackets;
	private final ConcurrentIndexMap<Collection<PacketHandler>> clientInitHandlers, clientStatusHandlers, clientLoginHandlers, clientPlayHandlers;

	public PacketsManagerImpl(KeyPair keyPair) throws GeneralSecurityException {
		this.authenticator = new Authenticator(keyPair);

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
		log.debug("Send packet {} to the client {}", packet, client);
		SERVER.networkThread.outboundQueue().add(new PacketSending(packet, (ClientImpl) client));
	}

	@Override
	public void sendPacket(Packet packet, Client... clients) {
		log.debug("Send packet {} to multiple clients: {}", packet, clients);
		for (Client client : clients) {
			SERVER.networkThread.outboundQueue().add(new PacketSending(packet, (ClientImpl) client));
		}
	}

	@Override
	public void sendPacket(Packet packet, Client client, Runnable completionAction) {
		log.debug("Send packet {} to the client {} with a completion action {}", packet, client, completionAction);
		SERVER.networkThread.outboundQueue().add(new PacketSending(packet, (ClientImpl) client, completionAction));
	}

	@Override
	public Packet parsePacket(ByteBuffer data, ConnectionState connState, boolean serverBound) {
		log.debug("parse packet: {}, state: {}, serverBound: {}", data, connState, serverBound);
		int packetId = ProtocolHelper.readVarInt(data);
		try {
			Class<? extends Packet> packetClass = getRegisteredPacket(connState, serverBound, packetId);
			Packet packet = packetClass.newInstance();
			return packet.readFrom(data);
		} catch (Exception ex) {
			log.error("Cannot create packet object with id {}", packetId, ex);
		}
		return null;
	}

	@Override
	public void handle(Packet packet, Client client) {
		log.debug("Handling packet {} from {}", packet.toString(), client.getAddress().toString());
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

		serverPlayPackets.put(0x00, org.mcphoton.network.play.serverbound.TeleportConfirmPacket.class);
		serverPlayPackets.put(0x01, org.mcphoton.network.play.serverbound.TabCompletePacket.class);
		serverPlayPackets.put(0x02, org.mcphoton.network.play.serverbound.ChatMessagePacket.class);
		serverPlayPackets.put(0x03, org.mcphoton.network.play.serverbound.ClientStatusPacket.class);
		serverPlayPackets.put(0x04, org.mcphoton.network.play.serverbound.ClientSettingsPacket.class);
		serverPlayPackets.put(0x05, org.mcphoton.network.play.serverbound.ConfirmTransactionPacket.class);
		serverPlayPackets.put(0x06, org.mcphoton.network.play.serverbound.EnchantItemPacket.class);
		serverPlayPackets.put(0x07, org.mcphoton.network.play.serverbound.ClickWindowPacket.class);
		serverPlayPackets.put(0x08, org.mcphoton.network.play.serverbound.CloseWindowPacket.class);
		serverPlayPackets.put(0x09, org.mcphoton.network.play.serverbound.PluginMessagePacket.class);
		serverPlayPackets.put(0x0A, org.mcphoton.network.play.serverbound.UseEntityPacket.class);
		serverPlayPackets.put(0x0B, org.mcphoton.network.play.serverbound.KeepAlivePacket.class);
		serverPlayPackets.put(0x0C, org.mcphoton.network.play.serverbound.PlayerPositionPacket.class);
		serverPlayPackets.put(0x0D, org.mcphoton.network.play.serverbound.PlayerPositionAndLookPacket.class);
		serverPlayPackets.put(0x0E, org.mcphoton.network.play.serverbound.PlayerLookPacket.class);
		serverPlayPackets.put(0x0F, org.mcphoton.network.play.serverbound.PlayerPacket.class);
		serverPlayPackets.put(0x10, org.mcphoton.network.play.serverbound.VehicleMovePacket.class);
		serverPlayPackets.put(0x11, org.mcphoton.network.play.serverbound.SteerBoatPacket.class);
		serverPlayPackets.put(0x12, org.mcphoton.network.play.serverbound.PlayerAbilitiesPacket.class);
		serverPlayPackets.put(0x13, org.mcphoton.network.play.serverbound.PlayerDiggingPacket.class);
		serverPlayPackets.put(0x14, org.mcphoton.network.play.serverbound.EntityActionPacket.class);
		serverPlayPackets.put(0x15, org.mcphoton.network.play.serverbound.SteerVehiclePacket.class);
		serverPlayPackets.put(0x16, org.mcphoton.network.play.serverbound.ResourcePackStatusPacket.class);
		serverPlayPackets.put(0x17, org.mcphoton.network.play.serverbound.HeldItemChangePacket.class);
		serverPlayPackets.put(0x18, org.mcphoton.network.play.serverbound.CreativeInventoryActionPacket.class);
		serverPlayPackets.put(0x19, org.mcphoton.network.play.serverbound.UpdateSignPacket.class);
		serverPlayPackets.put(0x1A, org.mcphoton.network.play.serverbound.AnimationPacket.class);
		serverPlayPackets.put(0x1B, org.mcphoton.network.play.serverbound.SpectatePacket.class);
		serverPlayPackets.put(0x1C, org.mcphoton.network.play.serverbound.PlayerBlockPlacementPacket.class);
		serverPlayPackets.put(0x1D, org.mcphoton.network.play.serverbound.UseItemPacket.class);

		clientStatusPackets.put(0, org.mcphoton.network.status.clientbound.ResponsePacket.class);
		clientStatusPackets.put(1, org.mcphoton.network.status.clientbound.PongPacket.class);

		clientLoginPackets.put(0, org.mcphoton.network.login.clientbound.DisconnectPacket.class);
		clientLoginPackets.put(1, org.mcphoton.network.login.clientbound.EncryptionRequestPacket.class);
		clientLoginPackets.put(2, org.mcphoton.network.login.clientbound.LoginSuccessPacket.class);
		clientLoginPackets.put(3, org.mcphoton.network.login.clientbound.SetCompressionPacket.class);

		clientPlayPackets.put(0x00, org.mcphoton.network.play.clientbound.SpawnObjectPacket.class);
		clientPlayPackets.put(0x01, org.mcphoton.network.play.clientbound.SpawnExperienceOrbPacket.class);
		clientPlayPackets.put(0x02, org.mcphoton.network.play.clientbound.SpawnGlobalEntityPacket.class);
		clientPlayPackets.put(0x03, org.mcphoton.network.play.clientbound.SpawnMobPacket.class);
		clientPlayPackets.put(0x04, org.mcphoton.network.play.clientbound.SpawnPaintingPacket.class);
		clientPlayPackets.put(0x05, org.mcphoton.network.play.clientbound.SpawnPlayerPacket.class);
		clientPlayPackets.put(0x06, org.mcphoton.network.play.clientbound.AnimationPacket.class);
		clientPlayPackets.put(0x07, org.mcphoton.network.play.clientbound.StatisticsPacket.class);
		clientPlayPackets.put(0x08, org.mcphoton.network.play.clientbound.BlockBreakAnimationPacket.class);
		clientPlayPackets.put(0x09, org.mcphoton.network.play.clientbound.UpdateBlockEntityPacket.class);
		clientPlayPackets.put(0x0A, org.mcphoton.network.play.clientbound.BlockActionPacket.class);
		clientPlayPackets.put(0x0B, org.mcphoton.network.play.clientbound.BlockChangePacket.class);
		clientPlayPackets.put(0x0C, org.mcphoton.network.play.clientbound.BossBarPacket.class);
		clientPlayPackets.put(0x0D, org.mcphoton.network.play.clientbound.ServerDifficultyPacket.class);
		clientPlayPackets.put(0x0E, org.mcphoton.network.play.clientbound.TabCompletePacket.class);
		clientPlayPackets.put(0x0F, org.mcphoton.network.play.clientbound.ChatMessagePacket.class);
		clientPlayPackets.put(0x10, org.mcphoton.network.play.clientbound.MultiBlockChangePacket.class);
		clientPlayPackets.put(0x11, org.mcphoton.network.play.clientbound.ConfirmTransactionPacket.class);
		clientPlayPackets.put(0x12, org.mcphoton.network.play.clientbound.CloseWindowPacket.class);
		clientPlayPackets.put(0x13, org.mcphoton.network.play.clientbound.OpenWindowPacket.class);
		clientPlayPackets.put(0x14, org.mcphoton.network.play.clientbound.WindowItemsPacket.class);
		clientPlayPackets.put(0x15, org.mcphoton.network.play.clientbound.WindowPropertyPacket.class);
		clientPlayPackets.put(0x16, org.mcphoton.network.play.clientbound.SetSlotPacket.class);
		clientPlayPackets.put(0x17, org.mcphoton.network.play.clientbound.SetCooldownPacket.class);
		clientPlayPackets.put(0x18, org.mcphoton.network.play.clientbound.PluginMessagePacket.class);
		clientPlayPackets.put(0x19, org.mcphoton.network.play.clientbound.NamedSoundEffectPacket.class);
		clientPlayPackets.put(0x1A, org.mcphoton.network.play.clientbound.DisconnectPacket.class);
		clientPlayPackets.put(0x1B, org.mcphoton.network.play.clientbound.EntityStatusPacket.class);
		clientPlayPackets.put(0x1C, org.mcphoton.network.play.clientbound.ExplosionPacket.class);
		clientPlayPackets.put(0x1D, org.mcphoton.network.play.clientbound.UnloadChunkPacket.class);
		clientPlayPackets.put(0x1E, org.mcphoton.network.play.clientbound.ChangeGameStatePacket.class);
		clientPlayPackets.put(0x1F, org.mcphoton.network.play.clientbound.KeepAlivePacket.class);
		clientPlayPackets.put(0x20, org.mcphoton.network.play.clientbound.ChunkDataPacket.class);
		clientPlayPackets.put(0x21, org.mcphoton.network.play.clientbound.EffectPacket.class);
		clientPlayPackets.put(0x22, org.mcphoton.network.play.clientbound.ParticlePacket.class);
		clientPlayPackets.put(0x23, org.mcphoton.network.play.clientbound.JoinGamePacket.class);
		clientPlayPackets.put(0x24, org.mcphoton.network.play.clientbound.MapPacket.class);
		clientPlayPackets.put(0x25, org.mcphoton.network.play.clientbound.EntityRelativeMovePacket.class);
		clientPlayPackets.put(0x26, org.mcphoton.network.play.clientbound.EntityLookAndRelativeMovePacket.class);
		clientPlayPackets.put(0x27, org.mcphoton.network.play.clientbound.EntityLookPacket.class);
		clientPlayPackets.put(0x28, org.mcphoton.network.play.clientbound.EntityPacket.class);
		clientPlayPackets.put(0x29, org.mcphoton.network.play.clientbound.VehicleMovePacket.class);
		clientPlayPackets.put(0x2A, org.mcphoton.network.play.clientbound.OpenSignEditor.class);
		clientPlayPackets.put(0x2B, org.mcphoton.network.play.clientbound.PlayerAbilitiesPacket.class);
		clientPlayPackets.put(0x2C, org.mcphoton.network.play.clientbound.CombatEventPacket.class);
		clientPlayPackets.put(0x2D, org.mcphoton.network.play.clientbound.PlayerListItemPacket.class);
		clientPlayPackets.put(0x2E, org.mcphoton.network.play.clientbound.PlayerPositionAndLookPacket.class);
		clientPlayPackets.put(0x2F, org.mcphoton.network.play.clientbound.UseBedPacket.class);
		clientPlayPackets.put(0x30, org.mcphoton.network.play.clientbound.PlayerPositionAndLookPacket.class);
		// TODO
		clientPlayPackets.put(0x43, org.mcphoton.network.play.clientbound.SpawnPositionPacket.class);
	}

	public void registerPacketHandlers() {
		serverInitHandlers.put(0, new SimpleBag(new HandshakeHandler()));

		serverStatusHandlers.put(0, new SimpleBag(new RequestHandler()));
		serverStatusHandlers.put(1, new SimpleBag(new PingHandler(this)));

		serverLoginHandlers.put(0, new SimpleBag(new LoginStartHandler(new Random(), authenticator, this)));
		serverLoginHandlers.put(1, new SimpleBag(new EncryptionResponseHandler(authenticator, this)));
	}

}
