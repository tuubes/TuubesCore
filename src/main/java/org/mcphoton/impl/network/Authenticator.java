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

import com.electronwill.json.Json;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.mcphoton.network.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author TheElectronWill
 */
public class Authenticator {

	private static final Logger log = LoggerFactory.getLogger(Authenticator.class);
	private static final String AUTH_URL = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=%s&serverId=%s";

	/**
	 * The ExecutorService that executes http requests in the background.
	 */
	private final ExecutorService backgroundHttpService = Executors.newSingleThreadExecutor(new NamedThreadFactory());
	private final Map<Client, byte[]> verifyTokens = new HashMap<>();
	private final Map<Client, String> usernames = new HashMap<>();
	private final KeyPair rsaKeyPair;
	private final byte[] publicKey;
	private final Cipher decryptCipher;

	public Authenticator(KeyPair rsaKeyPair) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		this.rsaKeyPair = rsaKeyPair;
		this.publicKey = rsaKeyPair.getPublic().getEncoded();
		this.decryptCipher = Cipher.getInstance("RSA");
		this.decryptCipher.init(Cipher.DECRYPT_MODE, rsaKeyPair.getPrivate());
	}

	public void store(String username, Client client) {
		usernames.put(client, username);
	}

	public void store(byte[] verifyToken, Client client) {
		verifyTokens.put(client, verifyToken);
	}

	public boolean checkToken(byte[] verifyToken, Client client) {
		return Arrays.equals(verifyToken, verifyTokens.get(client));
	}

	public boolean checkAndForgetToken(byte[] verifyToken, Client client) {
		return Arrays.equals(verifyToken, verifyTokens.remove(client));
	}

	public String getUsername(Client client) {
		return usernames.get(client);
	}

	public String getAndForgetUsername(Client client) {
		return usernames.remove(client);
	}

	public byte[] getEncodedPublicKey() {
		return publicKey;
	}

	public void authenticate(String username, byte[] sharedKey, Consumer<Map<String, Object>> onSuccess, Consumer<Exception> onFailure) {
		log.debug("Submitting authentication task for user {}", username);
		backgroundHttpService.submit(() -> {
			try {
				MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
				sha1.update(sharedKey);
				sha1.update(publicKey);
				byte[] digest = sha1.digest();
				String hexDigest = new BigInteger(digest).toString(16);

				URL url = new URL(String.format(AUTH_URL, username, hexDigest));
				InputStreamReader reader = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8);
				Map<String, Object> response = Json.readObject(reader);
				reader.close();
				onSuccess.accept(response);
			} catch (Exception ex) {
				onFailure.accept(ex);
			}

		});
	}

	public byte[] decryptWithRsaPrivateKey(byte[] data) throws IllegalBlockSizeException, BadPaddingException {
		return decryptCipher.doFinal(data);
	}

	private class NamedThreadFactory implements ThreadFactory {

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "http-auth");
		}

	}

}
