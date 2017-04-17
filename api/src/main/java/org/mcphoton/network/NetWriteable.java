package org.mcphoton.network;

import java.io.IOException;

/**
 * A {@code NetWriteable} is an object that can be written to a {@link NetOutput}, which allows it to be sent over the network.
 *
 * @author TheElectronWill
 */
public interface NetWriteable {
    /**
     * Writes this object to the specified output buffer.
     *
     * @param out the NetOutput to write this object to.
     * @throws IOException if an I/O error occurs.
     */
    void writeTo(NetOutput out) throws IOException;
}
