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

import org.mcphoton.network.Client;
import org.mcphoton.network.PacketHandler;
import org.mcphoton.network.PacketsManager;
import org.mcphoton.network.status.clientbound.PongPacket;
import org.mcphoton.network.status.serverbound.PingPacket;

/**
 *
 * @author TheElectronWill
 */
public class PingHandler implements PacketHandler<PingPacket> {

	private final PacketsManager pm;

	public PingHandler(PacketsManager pm) {
		this.pm = pm;
	}

	@Override
	public void handle(PingPacket packet, Client client) {
		PongPacket pong = new PongPacket();
		pong.payload = packet.payload;
		pm.sendPacket(pong, client);
	}

}
