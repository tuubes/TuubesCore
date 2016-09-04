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
package org.mcphoton.impl.server;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import org.mcphoton.Photon;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.PhotonLogger;

/**
 * The main class which launches the program.
 *
 * @author TheElectronWill
 */
public final class Main {

	/**
	 * The global Logger.
	 */
	public static final PhotonLogger LOGGER = (PhotonLogger) LoggerFactory.getLogger("PhotonServer");
	
	/**
	 * The unique server instance.
	 */
	public static final PhotonServer SERVER;

	static {
		printFramed("Photon server version " + Photon.getVersion(), "For minecraft version " + Photon.getMinecraftVersion());
		LOGGER.info("Generating RSA keypair for secure communications...");
		KeyPair keys = generateRsaKeyPair();
		SERVER = createServerInstance(keys);
	}

	private static KeyPair generateRsaKeyPair() {
		LOGGER.info("Generating RSA keypair...");
		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(512);
			return generator.genKeyPair();
		} catch (NoSuchAlgorithmException ex) {
			LOGGER.error("Cannot generate RSA keypair.", ex);
			System.exit(2);
		}
		return null;
	}

	private static PhotonServer createServerInstance(KeyPair keys) {
		LOGGER.info("Creating server instance...");
		try {
			return new PhotonServer(LOGGER, keys);
		} catch (Exception ex) {
			LOGGER.error("Cannot create the server instance.", ex);
			System.exit(3);
		}
		return null;
	}

	public static void main(String[] args) {
		SERVER.setShutdownHook();
		SERVER.registerCommands();
		SERVER.registerPackets();
		SERVER.loadPlugins();
		SERVER.startThreads();
	}

	private static void printFramed(String... strings) {
		int max = 0;
		for (String s : strings) {
			if (s.length() > max) {
				max = s.length();
			}
		}

		for (int i = 0; i < max + 4; i++) {
			System.out.print('-');
		}
		System.out.println();

		for (String s : strings) {
			System.out.print("| ");
			System.out.print(s);
			for (int i = 0; i < max - s.length(); i++) {
				System.out.print(' ');
			}
			System.out.println(" |");
		}

		for (int i = 0; i < max + 4; i++) {
			System.out.print('-');
		}
		System.out.println();
	}

}
