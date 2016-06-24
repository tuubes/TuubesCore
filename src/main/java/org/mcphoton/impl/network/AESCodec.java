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
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * A codec that encrypts and decrypts data with AES.
 *
 * @author TheElectronWill
 */
public class AESCodec implements Codec {

	private final SecretKey key;
	private final Cipher encryptCipher, decryptCipher;

	public AESCodec(byte[] keyBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
		key = new SecretKeySpec(keyBytes, "AES");

		encryptCipher = Cipher.getInstance("AES/CFB8/NoPadding");
		encryptCipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(key.getEncoded()));

		decryptCipher = Cipher.getInstance("AES/CFB8/NoPadding");
		decryptCipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(key.getEncoded()));
	}

	@Override
	public ByteBuffer encode(ByteBuffer data) throws GeneralSecurityException {
		ByteBuffer output = data.slice();
		encryptCipher.doFinal(data, output);
		output.flip();
		return output;
	}

	@Override
	public ByteBuffer decode(ByteBuffer data) throws GeneralSecurityException {
		ByteBuffer output = data.slice();
		decryptCipher.doFinal(data, output);
		output.flip();
		return output;
	}

}
