package org.mcphoton.impl.network;

import java.nio.ByteBuffer;

public final class NoCodec implements Codec {
	
	@Override
	public ByteBuffer encode(ByteBuffer data) {
		return data;
	}
	
	@Override
	public ByteBuffer decode(ByteBuffer data) {
		return data;
	}
	
}
