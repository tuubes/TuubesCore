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

import java.util.Objects;
import org.mcphoton.network.Packet;

/**
 * Contains informations about a packet to send to a client.
 *
 * @author TheElectronWill
 */
public final class PacketSending {

	private static final Runnable NO_ACTION = () -> {
	};

	/**
	 * The packet to send.
	 */
	public final Packet packet;

	/**
	 * The client to send the packet.
	 */
	public final ClientImpl recipient;

	/**
	 * The action to execute when the sending completes.
	 */
	public final Runnable completionAction;

	/**
	 * Creates a new PacketSending. The completionAction may not be null, use the other constructor to specify
	 * "no action".
	 *
	 * @param packet the packet to send.
	 * @param recipient the client to send the packet.
	 * @param completionAction the action to execute when the sending completes.
	 */
	public PacketSending(Packet packet, ClientImpl recipient, Runnable completionAction) {
		this.packet = packet;
		this.recipient = recipient;
		this.completionAction = Objects.requireNonNull(completionAction);
	}

	/**
	 * Creates a new PacketSending.
	 *
	 * @param packet the packet to send.
	 * @param recipient the client to send the packet.
	 */
	public PacketSending(Packet packet, ClientImpl recipient) {
		this(packet, recipient, NO_ACTION);
	}

	@Override
	public String toString() {
		return "PacketSending{" + "packet=" + packet + ", recipient=" + recipient + ", completionAction=" + completionAction + '}';
	}

}
