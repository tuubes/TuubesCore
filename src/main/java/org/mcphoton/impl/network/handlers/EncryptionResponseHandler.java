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

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.mcphoton.Photon;
import org.mcphoton.impl.entity.PlayerImpl;
import org.mcphoton.impl.network.AESCodec;
import org.mcphoton.impl.network.Authenticator;
import org.mcphoton.impl.network.ClientImpl;
import org.mcphoton.impl.server.Main;
import org.mcphoton.impl.world.ChunkColumnImpl;
import org.mcphoton.impl.world.WorldImpl;
import org.mcphoton.messaging.TextChatMessage;
import org.mcphoton.network.ByteArrayProtocolOutputStream;
import org.mcphoton.network.Client;
import org.mcphoton.network.PacketHandler;
import org.mcphoton.network.PacketsManager;
import org.mcphoton.network.login.clientbound.DisconnectPacket;
import org.mcphoton.network.login.clientbound.LoginSuccessPacket;
import org.mcphoton.network.login.serverbound.EncryptionResponsePacket;
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
public class EncryptionResponseHandler implements PacketHandler<EncryptionResponsePacket> {

	private static final Logger log = LoggerFactory.getLogger(EncryptionResponseHandler.class);
	private static final org.mcphoton.network.login.clientbound.DisconnectPacket BAD_VERIFY_TOKEN = new DisconnectPacket(), AUTH_FAILED = new DisconnectPacket();
	private static final Pattern UUID_FIXER = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

	static {
		BAD_VERIFY_TOKEN.reason = new TextChatMessage("Bad verify token.");
		AUTH_FAILED.reason = new TextChatMessage("Authentification failed.");
	}

	private final Authenticator authenticator;
	private final PacketsManager pm;

	public EncryptionResponseHandler(Authenticator authenticator, PacketsManager pm) {
		this.authenticator = authenticator;
		this.pm = pm;
	}

	@Override
	public void handle(EncryptionResponsePacket packet, Client client) {
		//--- Check the token ---
		byte[] verifyToken;
		try {
			verifyToken = authenticator.decryptWithRsaPrivateKey(packet.verifyToken);
		} catch (IllegalBlockSizeException | BadPaddingException ex) {
			log.error("Unable to decrypt the verify token sent by client {}.", client, ex);
			return;
		}
		if (!authenticator.checkAndForgetToken(verifyToken, client)) {
			pm.sendPacket(BAD_VERIFY_TOKEN, client);
			return;
		}

		//--- Authenticate the player ---
		byte[] sharedKey;
		try {
			sharedKey = authenticator.decryptWithRsaPrivateKey(packet.sharedKey);
		} catch (IllegalBlockSizeException | BadPaddingException ex) {
			log.error("Unable to decrypt the shared key sent by client {}.", client, ex);
			return;
		}
		String username = authenticator.getAndForgetUsername(client);
		ClientImpl cImpl = (ClientImpl) client;
		authenticator.authenticate(username, sharedKey, new AuthSuccessHandler(cImpl, username, sharedKey), new AuthFailureHandler(cImpl, username));
	}

	private class AuthSuccessHandler implements Consumer<Map<String, Object>> {

		private final ClientImpl client;
		private final String clientName;
		private final byte[] sharedKey;

		public AuthSuccessHandler(ClientImpl client, String clientName, byte[] sharedKey) {
			this.client = client;
			this.clientName = clientName;
			this.sharedKey = sharedKey;
		}

		@Override
		public void accept(Map<String, Object> response) {
			if (response.isEmpty()) {//bad auth
				pm.sendPacket(AUTH_FAILED, client);
				return;
			}
			//--- Get informations about the player ---
			/* TODO read skin. See wiki.vg for a description of the format used.
			 * TODO set the last player location if available
			 * TODO check if player whitelister and not banned
			 */

			String playerUuid = (String) response.get("id");
			String playerName = (String) response.get("name");
			log.trace("Got id={} from auth server", playerUuid);
			log.trace("Got name={} from auth server", playerName);

			if (playerUuid.indexOf('-') == -1) {//uuid without hyphens
				playerUuid = UUID_FIXER.matcher(playerUuid).replaceFirst("$1-$2-$3-$4-$5");
				log.debug("Fixed UUID: {}", playerUuid);
			}
			UUID accountId = UUID.fromString(playerUuid);

			//--- Enable encryption ---
			try {
				AESCodec cipherCodec = new AESCodec(sharedKey);
				client.enableEncryption(cipherCodec);
				log.debug("Encryption enabled for client {}", client);
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException ex) {
				log.error("Failed to enable encryption for client {}.", client, ex);
				pm.sendPacket(AUTH_FAILED, client, () -> {
					try {
						client.closeConnection();
					} catch (IOException ex1) {
						ex1.printStackTrace();
					}
				});
				return;
			}

			//--- Finish join sequence ---
			Location spawnLocation = Main.SERVER.spawn;
			final String finalPlayerUUID = playerUuid;//TODO improve :|
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
				abilitiesPacket.flags = 0;
				abilitiesPacket.flyingSpeed = 1f;
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
							ChunkColumnImpl chunk = (ChunkColumnImpl) wi.getChunksManager().getChunk(cx, cz, true);
							ChunkDataPacket chunkDataPacket = new ChunkDataPacket(cx, cz, chunk.getSections(), chunk.getBiomes());
							pm.sendPacket(chunkDataPacket, client);
						} catch (Exception ex) {
							log.error("Unable to send chunk {}  {}", cx, cz, ex);
						}
					}
				}
				log.trace("Join sequence completed!");
			});
		}

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

	private class AuthFailureHandler implements Consumer<Exception> {

		private final ClientImpl client;
		private final String clientName;

		AuthFailureHandler(ClientImpl client, String clientName) {
			this.client = client;
			this.clientName = clientName;
		}

		@Override
		public void accept(Exception ex) {
			pm.sendPacket(AUTH_FAILED, client);
			log.error("Unable to authenticate {}.", clientName, ex);
		}

	}

}
