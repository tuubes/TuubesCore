package org.mcphoton.impl.network;

import java.nio.ByteBuffer;

/**
 * A codec is a component that encodes and decodes data. It takes a ByteBuffer as input, works with it and returns the
 * result as a ByteBuffer.
 * 
 * @author TheElectronWill
 * 		
 */
public interface Codec {
	
	/**
	 * Encodes some data. The returned ByteBuffer may be a new one, or the <code>data</code> one. Its position will be 0
	 * and its limit will be correctly set.
	 * 
	 * @param data a ByteBuffer that contains the data to encode. Its position should be 0.
	 * @return a ByteBuffer that contains the encoded data
	 */
	ByteBuffer encode(ByteBuffer data) throws Exception;
	
	/**
	 * Decodes some data. The returned ByteBuffer may be a new one, or the <code>data</code> one. Its position will be 0
	 * and its limit will be correctly set.
	 * 
	 * @param data a ByteBuffer that contains the data to decode. Its position should be 0.
	 * @return a ByteBuffer that contains the decoded data
	 */
	ByteBuffer decode(ByteBuffer data) throws Exception;
	
}
