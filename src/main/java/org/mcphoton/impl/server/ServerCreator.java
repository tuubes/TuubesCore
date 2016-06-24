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

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import javax.imageio.ImageIO;
import org.mcphoton.config.ConfigurationSpecification;
import org.mcphoton.config.TomlConfiguration;
import org.mcphoton.utils.PhotonFavicon;
import org.mcphoton.world.Location;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.LoggingLevel;
import org.slf4j.impl.PhotonLogger;

/**
 * Configures and creates a server instance.
 *
 * @author TheElectronWill
 */
public class ServerCreator {

	private final File configFile = new File("serverConfig.toml");
	private final File faviconFile = new File("server-icon.png");
	private final ConfigurationSpecification configSpec = new ConfigurationSpecification();
	private final TomlConfiguration config = new TomlConfiguration();
	private final PhotonLogger logger;

	private InetSocketAddress address;
	private int maxPlayers;
	private String motd, encodedFavicon;
	private KeyPair keyPair;
	private LoggingLevel loggingLevel;

	public ServerCreator(String loggerName) {
		this.logger = (PhotonLogger) LoggerFactory.getLogger(loggerName);
	}

	private void specifyConfig() {
		configSpec.defineInt("port", 25565, 0, 65535);
		configSpec.defineInt("maxPlayers", 10, 1, 1000);
		configSpec.defineString("default-level", "world");
		configSpec.defineString("motd", "Photon server, version alpha");
		configSpec.defineString("loggingLevel", "DEBUG", "ERROR", "WARN", "INFO", "DEBUG", "TRACE");
	}

	private void loadConfig() {
		logger.info("Loading server configuration (serverConfig.toml).");
		try {
			if (configFile.exists()) {
				config.readFrom(configFile);
				int corrected = config.correct(configSpec);
				if (corrected > 0) {
					config.writeTo(configFile);
					logger.warn("Corrected {} entry(ies) in serverConfig.toml", corrected);
				}
				address = new InetSocketAddress(config.getInt("port"));
				maxPlayers = config.getInt("maxPlayers");
				motd = config.getString("motd");
				loggingLevel = LoggingLevel.valueOf(config.getString("loggingLevel"));
			} else {
				int corrected = config.correct(configSpec);
				logger.info("Added {} entries in serverConfig.toml", corrected);
				config.writeTo(configFile);
				loggingLevel = LoggingLevel.DEBUG;
			}
		} catch (IOException ex) {
			logger.error("Cannot load server configuration.", ex);
			System.exit(1);
		}
		logger.setLevel(loggingLevel);
	}

	private void loadFavicon() {
		PhotonFavicon favicon = new PhotonFavicon();
		if (faviconFile.exists()) {
			try {
				favicon.encode(ImageIO.read(faviconFile));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		encodedFavicon = favicon.getEncodedFavicon();
	}

	private void generateRsaKeyPair() {
		logger.info("Generating RSA keypair");
		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(512);
			keyPair = generator.genKeyPair();
		} catch (NoSuchAlgorithmException ex) {
			logger.error("Cannot generate RSA keypair.", ex);
			System.exit(2);
		}
	}

	PhotonServer createServer() {
		Location spawn = null;//TODO load from config
		specifyConfig();
		loadConfig();
		loadFavicon();
		generateRsaKeyPair();
		try {
			return new PhotonServer(logger, keyPair, address, motd, encodedFavicon, maxPlayers, spawn);
		} catch (Exception ex) {
			logger.error("Cannot create the server instance.", ex);
			System.exit(3);
			return null;
		}
	}

}
