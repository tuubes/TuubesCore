package org.tuubes.network;

import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.packet.Packet;

/**
 * @author TheElectronWill
 */
@FunctionalInterface
public interface PacketHandler {
	/**
	 * Handles a received or sent packet.
	 *
	 * @param packet  the packet
	 * @param session the packet's session
	 */
	void handle(Packet packet, Session session);
}
