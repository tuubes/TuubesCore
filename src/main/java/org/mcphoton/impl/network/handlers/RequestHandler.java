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
import static org.mcphoton.impl.server.Main.SERVER;
import org.mcphoton.network.Client;
import org.mcphoton.network.PacketHandler;
import org.mcphoton.network.handshaking.serverbound.HandshakePacket;
import org.mcphoton.network.status.clientbound.ResponsePacket;
import org.mcphoton.network.status.serverbound.RequestPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see http://wiki.vg/Server_List_Ping
 * @author TheElectronWill
 */
public class RequestHandler implements PacketHandler<RequestPacket> {

	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

	@Override
	public void handle(RequestPacket packet, Client client) {
		ResponsePacket response = new ResponsePacket();
		StringBuilder jsonBuilder = new StringBuilder("{");
		jsonBuilder.append("\"version\":{");
		jsonBuilder.append("\"name\":\"").append(Photon.getMinecraftVersion()).append("\",");
		jsonBuilder.append("\"protocol\":").append(HandshakePacket.CURRENT_PROTOCOL_VERSION);
		jsonBuilder.append("},");
		jsonBuilder.append("\"players\":{");
		jsonBuilder.append("\"max\":").append(SERVER.maxPlayers).append(',');
		jsonBuilder.append("\"online\":").append(SERVER.onlinePlayers.size());
		jsonBuilder.append("},");
		jsonBuilder.append("\"description\":{");
		jsonBuilder.append("\"text\":\"").append(SERVER.motd).append("\"");
		if (SERVER.encodedFavicon != null) {
			jsonBuilder.append("},");
			jsonBuilder.append("\"favicon\":\"").append(SERVER.encodedFavicon).append("\"");
			jsonBuilder.append("}");
		} else {
			jsonBuilder.append("}}");
		}
		response.jsonResponse = jsonBuilder.toString();
		log.debug("Sending ResponsePacket to the client: {}", jsonBuilder);
		SERVER.packetsManager.sendPacket(response, client);
	}

}
