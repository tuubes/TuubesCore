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
