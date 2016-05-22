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

import java.util.Collection;
import java.util.Collections;
import org.mcphoton.network.Packet;

/**
 * Contains informations about a packet to send to some clients.
 *
 * @author TheElectronWill
 */
public final class PacketSending {

	public final Packet packet;
	public final Collection<PhotonClient> clients;

	public PacketSending(Packet packet, PhotonClient client) {
		this.packet = packet;
		this.clients = Collections.singletonList(client);
	}

	public PacketSending(Packet packet, Collection<PhotonClient> clients) {
		this.packet = packet;
		this.clients = clients;
	}

}
