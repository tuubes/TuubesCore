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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import org.mcphoton.Photon;
import static org.mcphoton.Photon.*;
import org.mcphoton.config.ConfigurationSpecification;
import org.mcphoton.config.TomlConfiguration;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.LoggingLevel;
import org.slf4j.impl.PhotonLogger;

/**
 * The main class which launches the program.
 *
 * @author TheElectronWill
 */
public final class Main {

	/**
	 * The global ScheduledExecutorService.
	 */
	public static final ScheduledExecutorService EXECUTOR_SERVICE;
	/**
	 * The global Logger.
	 */
	public static final PhotonLogger LOGGER = (PhotonLogger) LoggerFactory.getLogger("PhotonServer");
	/**
	 * The unique server instance.
	 */
	public static final PhotonServer SERVER;

	//--- Loaded from the config ---
	private static InetSocketAddress address;
	private static String defaultWorld;
	private static String encodedFavicon;
	private static int executionThreads;
	private static KeyPair keyPair;
	private static LoggingLevel loggingLevel;
	private static int maxPlayers;
	private static String motd;
	private static double spawnX, spawnY, spawnZ;

	static {
		printFramed("Photon server version " + Photon.getVersion(), "For minecraft version " + Photon.getMinecraftVersion());
		readConfiguration();
		generateRsaKeyPair();
		EXECUTOR_SERVICE = Executors.newScheduledThreadPool(executionThreads, new ExecutionThreadFactory());
		PhotonServer server = null;
		try {
			server = new PhotonServer(LOGGER, keyPair, address, motd, encodedFavicon, maxPlayers, defaultWorld, spawnX, spawnY, spawnZ);
		} catch (Exception ex) {
			LOGGER.error("Cannot create the server instance.", ex);
			System.exit(3);
		}
		SERVER = server;
	}

	private static void generateRsaKeyPair() {
		LOGGER.info("Generating RSA keypair...");
		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(512);
			keyPair = generator.genKeyPair();
		} catch (NoSuchAlgorithmException ex) {
			LOGGER.error("Cannot generate RSA keypair.", ex);
			System.exit(2);
		}
	}

	private static void loadFavicon() {
		try {
			if (ICON_PNG.exists()) {
				SERVER.setFavicon(ImageIO.read(ICON_PNG));
			} else if (ICON_JPG.exists()) {
				SERVER.setFavicon(ImageIO.read(ICON_JPG));
			} else {
				SERVER.encodedFavicon = DEFAULT_ICON;
			}
		} catch (IOException ex) {
			LOGGER.error("Unable to load the favicon.", ex);
			SERVER.encodedFavicon = DEFAULT_ICON;
		}
	}

	public static void main(String[] args) {
		loadFavicon();
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

	private static void readConfiguration() {
		ConfigurationSpecification specification = new ConfigurationSpecification();
		specification.defineInt("port", 25565, 1, 65535);
		specification.defineInt("maxPlayers", 10, 1, 1000);
		specification.defineString("defaultWorld", "world");
		specification.defineString("spawn", "0,60,0");
		specification.defineString("motd", "Photon server, version alpha");
		specification.defineString("loggingLevel", "DEBUG", "ERROR", "WARN", "INFO", "DEBUG", "TRACE");
		specification.defineInt("executionThreads", Math.max(1, Runtime.getRuntime().availableProcessors() - 1), 1, 100);

		LOGGER.info("Loading the server's configuration from \"server_config.toml\"...");
		try {
			TomlConfiguration config;
			if (CONFIG_FILE.exists()) {
				config = new TomlConfiguration(CONFIG_FILE);
				int corrected = config.correct(specification);
				if (corrected > 0) {
					config.writeTo(CONFIG_FILE);
					LOGGER.warn("Corrected {} entry(ies) in server_config.toml", corrected);
				}

			} else {
				config = new TomlConfiguration();
				int corrected = config.correct(specification);
				LOGGER.info("Added {} entries in server_config.toml", corrected);
				config.writeTo(CONFIG_FILE);
			}
			address = new InetSocketAddress(config.getInt("port"));
			maxPlayers = config.getInt("maxPlayers");
			defaultWorld = config.getString("defaultWorld");
			String[] coords = config.getString("spawn").split(",");
			spawnX = Double.parseDouble(coords[0]);
			spawnY = Double.parseDouble(coords[1]);
			spawnZ = Double.parseDouble(coords[2]);
			motd = config.getString("motd");
			loggingLevel = LoggingLevel.valueOf(config.getString("loggingLevel"));
			executionThreads = config.getInt("executionThreads");
		} catch (IOException ex) {
			LOGGER.error("Cannot load the server's configuration.", ex);
			System.exit(1);
		}
		LOGGER.setLevel(loggingLevel);
	}

	private static class ExecutionThreadFactory implements ThreadFactory {

		private final AtomicInteger count = new AtomicInteger(1);

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "executor" + count.getAndIncrement());
		}
	}

}
