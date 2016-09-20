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
package org.mcphoton.impl.network.handlers;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.mcphoton.Photon;
import org.mcphoton.impl.entity.PlayerImpl;
import org.mcphoton.impl.network.Authenticator;
import org.mcphoton.impl.network.ClientImpl;
import org.mcphoton.impl.server.Main;
import org.mcphoton.impl.world.ChunkColumnImpl;
import org.mcphoton.impl.world.WorldImpl;
import org.mcphoton.network.ByteArrayProtocolOutputStream;
import org.mcphoton.network.Client;
import org.mcphoton.network.PacketHandler;
import org.mcphoton.network.PacketsManager;
import org.mcphoton.network.login.clientbound.LoginSuccessPacket;
import org.mcphoton.network.login.serverbound.LoginStartPacket;
import org.mcphoton.network.play.clientbound.ChunkDataPacket;
import org.mcphoton.network.play.clientbound.JoinGamePacket;
import org.mcphoton.network.play.clientbound.KeepAlivePacket;
import org.mcphoton.network.play.clientbound.PlayerAbilitiesPacket;
import org.mcphoton.network.play.clientbound.PlayerPositionAndLookPacket;
import org.mcphoton.network.play.clientbound.PluginMessagePacket;
import org.mcphoton.network.play.clientbound.ServerDifficultyPacket;
import org.mcphoton.network.play.clientbound.SpawnPositionPacket;
import org.mcphoton.utils.Location;
import org.mcphoton.world.Difficulty;
import org.mcphoton.world.WorldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author TheElectronWill
 */
public class LoginStartHandler implements PacketHandler<LoginStartPacket> {

	private static final Logger log = LoggerFactory.getLogger(LoginStartHandler.class);
	private final Random random;
	private final Authenticator authenticator;
	private final PacketsManager pm;

	public LoginStartHandler(Random random, Authenticator authenticator, PacketsManager pm) {
		this.random = random;
		this.authenticator = authenticator;
		this.pm = pm;
	}

	@Override
	public void handle(LoginStartPacket packet, Client client) {
		/* EncryptionRequestPacket requestPacket = new EncryptionRequestPacket();
		 * byte[] randomBytes = new byte[4];
		 * random.nextBytes(randomBytes);
		 * authenticator.store(randomBytes, client);
		 * authenticator.store(packet.username, client);
		 *
		 * requestPacket.serverId = "";
		 * requestPacket.verifyToken = randomBytes;
		 * requestPacket.publicKey = authenticator.getEncodedPublicKey();
		 * pm.sendPacket(requestPacket, client); */
		joinNow((ClientImpl) client);
	}

	private void joinNow(ClientImpl client) {
		//--- Finish join sequence ---
		final UUID accountId = UUID.randomUUID();
		final String clientName = "ElectronWill", playerName = "ElectronWill";
		final Location spawnLocation = Main.SERVER.spawn;
		final String finalPlayerUUID = accountId.toString();
		Photon.getExecutorService().execute(() -> {
			//Spawn
			log.trace("Spawning the player entity...");

			PlayerImpl player = new PlayerImpl(clientName, accountId, client);
			spawnLocation.getWorld().spawnEntity(player, spawnLocation);
			log.debug("Player instance created: {}", player);
			client.setPlayer(player);

			//LoginSuccess
			log.trace("Sending LoginSuccess...");
			LoginSuccessPacket loginSuccessPacket = new LoginSuccessPacket();
			loginSuccessPacket.username = playerName;
			loginSuccessPacket.uuid = finalPlayerUUID;
			pm.sendPacket(loginSuccessPacket, client);

			//Join game
			//TODO use real player values
			log.trace("Sending JoinGame...");
			JoinGamePacket joinGamePacket = new JoinGamePacket();
			joinGamePacket.difficulty = Difficulty.NORMAL.id;
			joinGamePacket.entityId = player.getEntityId();
			joinGamePacket.gamemode = 0;
			joinGamePacket.levelType = "default";
			joinGamePacket.maxPlayers = Photon.getServer().getMaxPlayers();
			joinGamePacket.reducedDebugInfo = false;
			joinGamePacket.worldType = WorldType.OVERWORLD.id;
			pm.sendPacket(joinGamePacket, client);

			//Server brand
			log.trace("Sending server brand (PluginMessage)...");
			ByteArrayProtocolOutputStream pos = new ByteArrayProtocolOutputStream();
			pos.writeString("Photon");
			PluginMessagePacket pluginMessagePacket = new PluginMessagePacket();
			pluginMessagePacket.channel = "MC|Brand";
			pluginMessagePacket.data = Arrays.copyOf(pos.getBytes(), pos.size());
			pm.sendPacket(pluginMessagePacket, client);

			//Difficulty
			//TODO use real world settings
			log.trace("Sending ServerDifficulty...");
			ServerDifficultyPacket difficultyPacket = new ServerDifficultyPacket();
			difficultyPacket.difficulty = Difficulty.NORMAL.id;
			pm.sendPacket(difficultyPacket, client);

			//Spawn position (player's respawn point aka "home")
			//TODO use real player values
			log.trace("Sending SpawnPosition...");
			SpawnPositionPacket spawnPositionPacket = new SpawnPositionPacket();
			spawnPositionPacket.x = spawnPositionPacket.y = spawnPositionPacket.z = 0;
			pm.sendPacket(spawnPositionPacket, client);

			//Abilities
			//TODO use real player values
			log.trace("Sending PlayerAbilities...");
			PlayerAbilitiesPacket abilitiesPacket = new PlayerAbilitiesPacket();
			abilitiesPacket.fieldViewModifier = 1f;
			abilitiesPacket.flags = 6;
			abilitiesPacket.flyingSpeed = 2f;
			pm.sendPacket(abilitiesPacket, client);

			//Position (where the player spawns now)
			log.trace("Sending PlayerPositionAndLook...");
			PlayerPositionAndLookPacket palPacket = new PlayerPositionAndLookPacket();
			palPacket.flags = 0;
			palPacket.pitch = palPacket.yaw = 0;
			palPacket.x = spawnLocation.getBlockX();
			palPacket.y = spawnLocation.getBlockY();
			palPacket.z = spawnLocation.getBlockZ();
			palPacket.teleportId = 0;
			pm.sendPacket(palPacket, client);
		});
		//KeepAlive
		log.trace("Starting to send KeepAlive packets...");
		Photon.getExecutorService().scheduleWithFixedDelay(new KeepClientAlive(client), 15, 15, TimeUnit.SECONDS);

		Photon.getExecutorService().execute(() -> {
			//--- Send block chunks ---
			log.trace("Sending block chunks...");
			WorldImpl wi = (WorldImpl) spawnLocation.getWorld();
			for (int cx = spawnLocation.getBlockX() / 32 - 1; cx <= spawnLocation.getBlockX() / 32 + 1; cx++) {
				for (int cz = spawnLocation.getBlockZ() / 32 - 1; cz <= spawnLocation.getBlockZ() / 32 + 1; cz++) {
					log.trace("Sending chunk {}  {}", cx, cz);
					try {
						String msg = "Chunk " + cx + "  " + cz + " sent!";
						ChunkColumnImpl chunk = (ChunkColumnImpl) wi.getChunksManager().getChunk(cx, cz, true);
						ChunkDataPacket chunkDataPacket = new ChunkDataPacket(cx, cz, chunk.getSections(), chunk.getBiomes());
						pm.sendPacket(chunkDataPacket, client, () -> System.out.println(msg));
					} catch (Exception ex) {
						log.error("Unable to send chunk {}  {}", cx, cz, ex);
					}
				}
			}
			log.trace("Join sequence completed!");
		});
	}

	private class KeepClientAlive implements Runnable {

		private final Client client;

		KeepClientAlive(Client client) {
			this.client = client;
		}

		@Override
		public void run() {
			KeepAlivePacket keepAlivePacket = new KeepAlivePacket();
			keepAlivePacket.keepAliveId = 0b1010;
			pm.sendPacket(keepAlivePacket, client);
		}

	}

}
