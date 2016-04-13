package org.mcphoton.impl.network;

import java.util.Collection;
import org.mcphoton.network.Packet;

public final class PacketSending {
	
	public final Packet packet;
	public final Collection<PhotonClient> clients;
	
	public PacketSending(Packet packet, Collection<PhotonClient> clients) {
		this.packet = packet;
		this.clients = clients;
	}
	
}
