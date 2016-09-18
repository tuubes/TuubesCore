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
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.mcphoton.impl.entity.PlayerImpl;
import org.mcphoton.impl.network.AESCodec;
import org.mcphoton.impl.network.Authenticator;
import org.mcphoton.impl.network.ClientImpl;
import org.mcphoton.impl.server.Main;
import org.mcphoton.messaging.TextChatMessage;
import org.mcphoton.network.Client;
import org.mcphoton.network.PacketHandler;
import org.mcphoton.network.PacketsManager;
import org.mcphoton.network.login.clientbound.DisconnectPacket;
import org.mcphoton.network.login.clientbound.LoginSuccessPacket;
import org.mcphoton.network.login.serverbound.EncryptionResponsePacket;
import org.mcphoton.utils.Location;
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
		authenticator.authenticate(username, sharedKey, (Map<String, Object> response) -> {//on success
			if (response.isEmpty()) {//bad auth
				pm.sendPacket(AUTH_FAILED, client);
				return;
			}
			ClientImpl clientImpl = (ClientImpl) client;
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

			Location location = Main.SERVER.spawn;

			PlayerImpl player = new PlayerImpl(username, accountId, clientImpl);
			location.getWorld().spawnEntity(player, location);
			log.debug("Player instance created: {}", player);
			
			clientImpl.setPlayer(player);

			//--- Enable encryption ---
			try {
				AESCodec cipherCodec = new AESCodec(sharedKey);
				clientImpl.enableEncryption(cipherCodec);
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

			//--- Send LoginSuccess packet ---
			LoginSuccessPacket loginSuccessPacket = new LoginSuccessPacket();
			loginSuccessPacket.username = playerName;
			loginSuccessPacket.uuid = playerUuid;
			pm.sendPacket(loginSuccessPacket, client);
			
			//--- Send ChunkData packets ---
			
		},
				(Exception ex) -> {//on failure
					pm.sendPacket(AUTH_FAILED, client);
					log.error("Unable to authenticate {}.", username, ex);
				}
		);
	}

}
