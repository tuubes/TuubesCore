package org.mcphoton.network;

import java.io.IOException;

/**
 * A network packet.
 */
public interface Packet extends NetWriteable {
    /**
     * Reads the packet from the given input buffer.
     *
     * @param in The input source to read from.
     */
    void readFrom(NetInput in) throws IOException;

    /**
     * Writes the packet to the given output buffer.
     */
    void writeTo(NetOutput out) throws IOException;
}
