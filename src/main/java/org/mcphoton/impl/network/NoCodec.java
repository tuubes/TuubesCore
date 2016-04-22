package org.mcphoton.impl.network;

import java.nio.ByteBuffer;

/**
 * A codec that does nothing.
 *
 * @author TheElectronWill
 */
public final class NoCodec implements Codec {

	/**
	 * Does nothing.
	 *
	 * @return the data buffer, as it is
	 */
	@Override
	public ByteBuffer encode(ByteBuffer data) {
		return data;
	}

	/**
	 * Does nothing.
	 *
	 * @return the data buffer, as it is
	 */
	@Override
	public ByteBuffer decode(ByteBuffer data) {
		return data;
	}

}
