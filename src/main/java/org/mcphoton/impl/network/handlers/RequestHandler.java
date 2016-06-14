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

import org.mcphoton.Photon;
import org.mcphoton.impl.server.PhotonServer;
import org.mcphoton.network.Client;
import org.mcphoton.network.PacketHandler;
import org.mcphoton.network.handshaking.serverbound.HandshakePacket;
import org.mcphoton.network.status.clientbound.ResponsePacket;
import org.mcphoton.network.status.serverbound.RequestPacket;

/**
 *
 * @author TheElectronWill
 */
public class RequestHandler implements PacketHandler<RequestPacket> {

	private final PhotonServer server;

	public RequestHandler(PhotonServer server) {
		this.server = server;
	}

	@Override
	public void handle(RequestPacket packet, Client client) {
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
		server.packetsManager.sendPacket(response, client);
	}

}
