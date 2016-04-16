package org.mcphoton.impl.server;

import org.mcphoton.impl.server.PhotonServer;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import org.mcphoton.config.ConfigurationSpecification;
import org.mcphoton.config.TomlConfiguration;
import org.mcphoton.impl.network.NetworkInputThread;
import org.mcphoton.impl.network.NetworkOutputThread;
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
	private final ConfigurationSpecification configSpec = new ConfigurationSpecification();
	private final TomlConfiguration config = new TomlConfiguration();
	private final PhotonLogger logger;

	private InetSocketAddress address;
	private int maxPlayers;
	private String motd;
	private KeyPair keyPair;
	private NetworkInputThread nit;
	private NetworkOutputThread not;
	private LoggingLevel loggingLevel;

	public ServerCreator(String loggerName) {
		this.logger = (PhotonLogger) LoggerFactory.getLogger(loggerName);
	}

	private void specifyConfig() {
		configSpec.defineInt("port", 25565, 0, 65535);
		configSpec.defineInt("maxPlayers", 10, 1, 1000);
		configSpec.defineString("motd", "Photon server, version alpha");
		configSpec.defineString("loggingLevel", "DEBUG", "ERROR", "WARN", "INFO", "DEBUG", "TRACE");
	}

	private void loadConfig() {
		logger.info("Loading server configuration (serverConfig.toml)");
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
			logger.error("Cannot load server configuration", ex);
			System.exit(1);
		}
		logger.setLevel(loggingLevel);
	}

	private void generateRsaKeyPair() {
		logger.info("Generating RSA keypair");
		try {
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(512);
			keyPair = generator.genKeyPair();
		} catch (NoSuchAlgorithmException ex) {
			logger.error("Cannot generate RSA keypair", ex);
			System.exit(2);
		}
	}

	private void createThreads() {
		logger.info("Creating threads");
		try {
			nit = new NetworkInputThread(address);
			not = new NetworkOutputThread(nit.getSelector(), nit);
		} catch (IOException ex) {
			logger.error("Cannot create network threads", ex);
			System.exit(3);
		}
	}

	PhotonServer createServer() {
		Location spawn = null;//TODO load from config
		specifyConfig();
		loadConfig();
		generateRsaKeyPair();
		createThreads();
		return new PhotonServer(logger, keyPair, address, nit, not, motd, maxPlayers, spawn);
	}

}
