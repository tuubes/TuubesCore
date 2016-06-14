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

import java.util.Random;
import org.mcphoton.impl.network.Authenticator;
import org.mcphoton.network.Client;
import org.mcphoton.network.PacketHandler;
import org.mcphoton.network.PacketsManager;
import org.mcphoton.network.login.clientbound.EncryptionRequestPacket;
import org.mcphoton.network.login.serverbound.LoginStartPacket;

/**
 *
 * @author TheElectronWill
 */
public class LoginStartHandler implements PacketHandler<LoginStartPacket> {

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
		EncryptionRequestPacket requestPacket = new EncryptionRequestPacket();
		byte[] randomBytes = new byte[4];
		random.nextBytes(randomBytes);
		authenticator.store(randomBytes, client);

		requestPacket.serverId = "";
		requestPacket.verifyToken = randomBytes;
		requestPacket.publicKey = authenticator.getEncodedPublicKey();
		pm.sendPacket(requestPacket, client);
	}

}
