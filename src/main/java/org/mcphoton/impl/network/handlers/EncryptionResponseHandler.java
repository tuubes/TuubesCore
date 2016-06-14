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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;
import javax.crypto.NoSuchPaddingException;
import org.mcphoton.impl.entity.PhotonPlayer;
import org.mcphoton.impl.network.AESCodec;
import org.mcphoton.impl.network.Authenticator;
import org.mcphoton.impl.network.PhotonClient;
import org.mcphoton.impl.server.Main;
import org.mcphoton.messaging.TextChatMessage;
import org.mcphoton.network.Client;
import org.mcphoton.network.PacketHandler;
import org.mcphoton.network.PacketsManager;
import org.mcphoton.network.login.clientbound.DisconnectPacket;
import org.mcphoton.network.login.clientbound.LoginSuccessPacket;
import org.mcphoton.network.login.serverbound.EncryptionResponsePacket;
import org.mcphoton.world.Location;

/**
 *
 * @author TheElectronWill
 */
public class EncryptionResponseHandler implements PacketHandler<EncryptionResponsePacket> {

	private static final org.mcphoton.network.login.clientbound.DisconnectPacket BAD_VERIFY_TOKEN = new DisconnectPacket(), AUTH_FAILED = new DisconnectPacket();

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
		if (!authenticator.checkAndForgetToken(packet.verifyToken, client)) {
			pm.sendPacket(BAD_VERIFY_TOKEN, client);
			return;
		}
		String username = authenticator.getAndForgetUsername(client);
		authenticator.authenticate(username, packet.sharedKey,
				(Map<String, Object> response) -> {//on success
					if (response.isEmpty()) {//bad auth
						pm.sendPacket(AUTH_FAILED, client);
						return;
					}
					PhotonClient pc = (PhotonClient) client;
					//--- Get informations about the player ---
					/* TODO read skin. See wiki.vg for a description of the format used.
					 * TODO set the last player location if available
					 * TODO check if player whitelister and not banned
					 */

					String playerUuid = (String) response.get("id");
					String playerName = (String) response.get("name");
					UUID accountId = UUID.fromString(playerUuid);
					Location location = Main.serverInstance.spawn;

					PhotonPlayer player = new PhotonPlayer(username, accountId, location);
					Main.serverInstance.logger.debug("Player instance created: {}", player);
					pc.setPlayer(player);

					//--- Enable encryption ---
					try {
						AESCodec cipherCodec = new AESCodec(packet.sharedKey);
						pc.enableEncryption(cipherCodec);
					} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ex) {
						Main.serverInstance.logger.error("Failed to enable encryption. Client: {}.", client, ex);
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
				},
				(Exception ex) -> {//on failure
					pm.sendPacket(AUTH_FAILED, client);
					Main.serverInstance.logger.error("Unable to authenticate {}.", username, ex);
				}
		);
	}

}
