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

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * A codec that encrypts and decrypts data with AES.
 *
 * @author TheElectronWill
 */
public class AESCodec implements Codec {

	private final SecretKey key;
	private final Cipher cipher;

	public AESCodec(SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException {
		this.key = key;
		cipher = Cipher.getInstance("AES/CFB8/NoPadding");// or AES/CFB8/PKCS5Padding or just AES/CFB8 or just AES ??
	}

	@Override
	public ByteBuffer encode(ByteBuffer data) throws Exception {
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return useCipher(data);
	}

	@Override
	public ByteBuffer decode(ByteBuffer data) throws Exception {
		cipher.init(Cipher.DECRYPT_MODE, key);
		return useCipher(data);
	}

	private ByteBuffer useCipher(ByteBuffer input) throws Exception {
		ByteBuffer output = input.slice();
		cipher.doFinal(input, output);
		output.flip();
		return output;
	}

}
