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

import org.mcphoton.impl.server.Main;
import org.mcphoton.network.Client;
import org.mcphoton.network.ConnectionState;
import org.mcphoton.network.PacketHandler;
import org.mcphoton.network.handshaking.serverbound.HandshakePacket;

/**
 *
 * @author TheElectronWill
 */
public class HandshakeHandler implements PacketHandler<HandshakePacket> {

	@Override
	public void handle(HandshakePacket packet, Client client) {
		Main.serverInstance.logger.debug("Set client state to " + packet.nextState);
		if (packet.nextState == 1) {
			client.setConnectionState(ConnectionState.STATUS);
		} else if (packet.nextState == 2) {
			client.setConnectionState(ConnectionState.LOGIN);
		} else {
			//invalid
		}
	}

}
