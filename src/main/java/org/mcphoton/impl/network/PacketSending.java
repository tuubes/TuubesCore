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
