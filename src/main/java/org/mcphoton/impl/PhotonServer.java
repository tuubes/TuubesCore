package org.mcphoton.impl;

import com.electronwill.utils.SimpleBag;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.mcphoton.config.ConfigurationSpecification;
import org.mcphoton.config.TomlConfiguration;
import org.mcphoton.entity.living.player.Player;
import org.mcphoton.impl.network.NetworkInputThread;
import org.mcphoton.impl.network.NetworkOutputThread;
import org.mcphoton.impl.plugin.PhotonPluginsManager;
import org.mcphoton.plugin.PluginsManager;
import org.mcphoton.server.BansManager;
import org.mcphoton.server.Server;
import org.mcphoton.server.WhitelistManager;
import org.mcphoton.world.Location;
import org.mcphoton.world.World;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.LoggingService;
import org.slf4j.impl.PhotonLogger;

/**
 * Serveur de jeu.
 *
 * @author TheElectronWill
 *
 */
public final class PhotonServer implements Server {

	private final KeyPair keyPair;
	private final File configFile = new File("serverConfig.toml");
	private final ConfigurationSpecification configSpec = new ConfigurationSpecification();
	private final TomlConfiguration config = new TomlConfiguration();
	private final PhotonLogger log = (PhotonLogger) LoggerFactory.getLogger("Photon");
	private final PluginsManager pm = new PhotonPluginsManager();

	private volatile InetSocketAddress address;
	private volatile NetworkInputThread networkInputThread;
	private volatile NetworkOutputThread networkOutputThread;

	private volatile String motd;

	private final Collection<Player> onlinePlayers = new SimpleBag<>();
	private volatile int maxPlayers;

	private final Map<String, World> worlds = new HashMap<>();
	private volatile Location spawn;

	public PhotonServer(KeyPair keyPair) {
		this.keyPair = keyPair;
		log.setLevel(PhotonLogger.LEVEL_DEBUG);
	}

	void specifyConfig() {
		configSpec.defineInt("port", 25565, 0, 65535);
		configSpec.defineInt("maxPlayers", 10, 1, 1000);
		configSpec.defineString("motd", "Photon server, version alpha");
	}

	void reloadConfig() throws IOException {
		log.info("Loading serverConfig.toml...");
		if (configFile.exists()) {
			config.readFrom(configFile);
			int corrected = config.correct(configSpec);
			if (corrected > 0) {
				config.writeTo(configFile);
				log.warn("Corrected {} entry(ies) in serverConfig.toml", corrected);
			}
			address = new InetSocketAddress(config.getInt("port"));
			maxPlayers = config.getInt("maxPlayers");
			motd = config.getString("motd");
		} else {
			int corrected = config.correct(configSpec);
			log.info("Added {} entries in serverConfig.toml", corrected);
			config.writeTo(configFile);
		}
	}

	void startThreads() {
		try {
			networkInputThread = new NetworkInputThread(address);
			networkOutputThread = new NetworkOutputThread(networkInputThread.getSelector(), networkInputThread);
			networkInputThread.start();
			networkOutputThread.start();
		} catch (IOException ex) {
			log.error("Cannot create network threads", ex);
		}

	}

	void setShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			LoggingService.close();
			networkInputThread.stopNicely();
			networkOutputThread.stopNicely();
			try {
				networkInputThread.join(1000);
				networkOutputThread.join(1000);
			} catch (InterruptedException ex) {
				log.error("Unable to close the network threads in an acceptable time", ex);
			}
		}));
	}

	@Override
	public Collection<Player> getOnlinePlayers() {
		return Collections.unmodifiableCollection(onlinePlayers);
	}

	@Override
	public int getMaxPlayers() {
		return maxPlayers;
	}

	@Override
	public Player getPlayer(UUID id) {
		for (Player p : onlinePlayers) {
			if (p.getAccountId().equals(id)) {
				return p;
			}
		}
		return null;
	}

	@Override
	public Player getPlayer(String name) {
		for (Player p : onlinePlayers) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}

	@Override
	public BansManager getBansManager() {
		return null;//TODO
	}

	@Override
	public WhitelistManager getWhitelistManager() {
		return null; //TODO
	}

	@Override
	public InetSocketAddress getBoundAddress() {
		return address;
	}

	@Override
	public boolean isOnlineMode() {
		return true;
	}

	@Override
	public Collection<World> getWorlds() {
		return Collections.unmodifiableCollection(worlds.values());
	}

	@Override
	public World getWorld(String name) {
		return worlds.get(name);
	}

	@Override
	public void registerWorld(World w) {
		worlds.put(w.getName(), w);
	}

	@Override
	public void unregisterWorld(World w) {
		worlds.remove(w.getName());
	}

	@Override
	public Location getSpawn() {
		return spawn;
	}

	@Override
	public void setSpawn(Location spawn) {
		this.spawn = spawn;
	}

}
