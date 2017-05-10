package org.mcphoton.network;

import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.packet.Packet;

/**
 * Interface for handling received (or sent) packets.
 *
 * @author TheElectronWill
 */
@FunctionalInterface
public interface PacketHandler<P extends Packet> {
	/**
	 * Handles a packet received by (or sent to) the specified session.
	 *
	 * @param packet  the packet
	 * @param session the session
	 */
	void handle(P packet, Session session);
}
