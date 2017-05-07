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

import com.electronwill.utils.Constant;
import com.electronwill.utils.IntConstant;
import com.electronwill.utils.SimpleBag;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.mcphoton.Photon;
import org.mcphoton.config.ConfigurationSpecification;
import org.mcphoton.config.TomlConfiguration;
import org.mcphoton.entity.living.Player;
import org.mcphoton.impl.command.ListCommand;
import org.mcphoton.impl.command.GlobalCommandRegistryImpl;
import org.mcphoton.impl.command.StopCommand;
import org.mcphoton.impl.network.NioNetworkThread;
import org.mcphoton.impl.network.PacketsManagerImpl;
import org.mcphoton.impl.plugin.GlobalPluginsManagerImpl;
import org.mcphoton.impl.world.WorldImpl;
import org.mcphoton.network.PacketsManager;
import org.mcphoton.plugin.GlobalPluginsManager;
import org.mcphoton.server.BansManager;
import org.mcphoton.server.Server;
import org.mcphoton.server.WhitelistManager;
import org.mcphoton.utils.Location;
import org.mcphoton.world.World;
import org.mcphoton.world.WorldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.LoggingService;
import org.slf4j.impl.PhotonLogger;

/**
 * The game server.
 *
 * @author TheElectronWill
 */
public final class ServerImpl implements Server {
	private static final Logger log = LoggerFactory.getLogger(ServerImpl.class);
	private static final File CONFIG_FILE = new File(Photon.MAIN_DIR, "server-config.toml");

	//---- Utilities ----
	public final ConsoleThread consoleThread = new ConsoleThread();
	public final GlobalPluginsManagerImpl pluginsManager = new GlobalPluginsManagerImpl();
	public final GlobalCommandRegistryImpl commandRegistry = new GlobalCommandRegistryImpl();
	public final Constant<ScheduledExecutorService> executorService = new Constant<>();

	//---- Configuration ----

	//---- Runtime data ----
	public final Collection<Player> onlinePlayers = new SimpleBag<>();
	public final Map<String, World> worlds = new ConcurrentHashMap<>();

	public ServerImpl() {
		loadConfig();
	}

	@Override
	public BansManager getBansManager() {
		return BansManagerImpl.getInstance();
	}

	@Override
	public Collection<Player> getOnlinePlayers() {
		return Collections.unmodifiableCollection(onlinePlayers);
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
	public GlobalPluginsManager getPluginsManager() {
		return pluginsManager;
	}

	@Override
	public Location getSpawn() {
		return spawn;
	}

	@Override
	public void setSpawn(Location spawn) {
		this.spawn = spawn;
	}

	@Override
	public WhitelistManager getWhitelistManager() {
		return WhitelistManagerImpl.getInstance();
	}

	@Override
	public World getWorld(String name) {
		return worlds.get(name);
	}

	@Override
	public Collection<World> getWorlds() {
		return Collections.unmodifiableCollection(worlds.values());
	}

	@Override
	public boolean isOnlineMode() {
		return true;
	}

	public void loadBanlist() {
		try {
			BansManagerImpl.getInstance().load();
		} catch (IOException ex) {
			log.error("Unable to load the ban list.", ex);
		}
	}

	public void saveBanlist() {
		try {
			BansManagerImpl.getInstance().save();
		} catch (IOException ex) {
			log.error("Unable to save the ban list.", ex);
		}
	}

	public void loadWhitelist() {
		try {
			WhitelistManagerImpl.getInstance().load();
		} catch (IOException ex) {
			log.error("Unable to load the whitelist.", ex);
		}
	}

	public void saveWhitelist() {
		try {
			WhitelistManagerImpl.getInstance().save();
		} catch (IOException ex) {
			log.error("Unable to save the whitelist.", ex);
		}
	}

	public void loadConfig() {
		log.info("Loading the server's configuration from server-config.toml ...");
		try {
			TomlConfiguration config;
			if (CONFIG_FILE.exists()) {
				config = new TomlConfiguration(CONFIG_FILE);
				int corrected = config.correct(CONFIG_SPEC);
				if (corrected > 0) {
					config.writeTo(CONFIG_FILE);
					log.warn("Corrected {} invalid entry(ies) in server-config.toml", corrected);
				}
			} else {
				config = new TomlConfiguration();
				int corrected = config.correct(CONFIG_SPEC);
				log.info("Added {} missing entries in server-config.toml", corrected);
				config.writeTo(CONFIG_FILE);
			}

			int port = config.getInt("port");
			if (!address.isInitialized()) {
				address.init(new InetSocketAddress(port));
			} else if (address.get().getPort() != port) {
				log.warn("The server port has been modified in the config file. A restart is required to make the change effective.");
			}

			maxPlayers = config.getInt("maxPlayers");
			WhitelistManagerImpl.getInstance().setEnabled(config.getBoolean("whitelist"));

			spawnWorldName = config.getString("world");
			String[] coords = config.getString("spawn").split(",");
			spawnX = Double.parseDouble(coords[0].trim());
			spawnY = Double.parseDouble(coords[1].trim());
			spawnZ = Double.parseDouble(coords[2].trim());

			motd = config.getString("motd");

			LoggingLevel logLevel = LoggingLevel.valueOf(config.getString("loggingLevel"));
			PhotonLogger.setLevel(logLevel);

			int threads = config.getInt("executionThreads");
			if (!executorService.isInitialized()) {
				executorService.init(Executors.newScheduledThreadPool(threads, new ExecutionThreadFactory()));
			} else if (executionThreads.get() != threads) {
				log.warn("The number of execution threads has been modified in the config file. A restart is required to make the change effective.");
			}

			log.info("Server config loaded!");
		} catch (IOException ex) {
			log.error("Cannot load the server's configuration.", ex);
			System.exit(1);
		}
	}

	void loadPlugins() {
		log.info("Loading plugins...");
		if (!Photon.PLUGINS_DIR.isDirectory()) {
			Photon.PLUGINS_DIR.mkdir();
		}
		try {
			pluginsManager.loadAllPlugins();
		} catch (IOException ex) {
			log.error("Unexpected error while trying to load the plugins.", ex);
		}
	}

	void loadWorlds() {
		log.info("Loading game worlds...");
		if (!Photon.WORLDS_DIR.isDirectory()) {
			Photon.WORLDS_DIR.mkdir();
		}
		for (File worldDir : Photon.WORLDS_DIR.listFiles((File f) -> f.isDirectory())) {
			World world = new WorldImpl(worldDir.getName(), WorldType.OVERWORLD);
			registerWorld(world);
		}
		World spawnWorld = worlds.get(spawnWorldName);
		if (spawnWorld == null) {
			log.info("Spawn world doesn't exist. Creating it...");
			File worldDir = new File(Photon.WORLDS_DIR, spawnWorldName);
			boolean dirCreated = worldDir.mkdirs();
			if (!dirCreated) {
				log.error("Unable to create a directory for the spawn world.");
			}
			spawnWorld = new WorldImpl(worldDir.getName(), WorldType.OVERWORLD);
			registerWorld(spawnWorld);
			log.info("Spawn world \"{}\" created.", spawnWorldName);
		}
		this.spawn = new Location(spawnX, spawnY, spawnZ, spawnWorld);
		log.info("{} worlds loaded!", worlds.size());
	}

	void registerCommands() {
		log.info("Registering photon commands...");
		commandRegistry.registerInternalCommand(new StopCommand());
		commandRegistry.registerInternalCommand(new ListCommand());
	}

	void registerPackets() {
		log.info("Registering game packets...");
		packetsManager.registerGamePackets();
		log.info("Registering packets handlers...");
		packetsManager.registerPacketHandlers();
	}

	@Override
	public void registerWorld(World w) {
		worlds.put(w.getName(), w);
	}

	public void saveConfig() {
		TomlConfiguration config = new TomlConfiguration();
		config.put("port", address.get().getPort());
		config.put("world", spawn.getWorld().getName());
		config.put("spawn", spawn.getX() + "," + spawn.getY() + "," + spawn.getZ());
		config.put("motd", motd);
		config.put("loggingLevel", PhotonLogger.getLevel());
		config.put("executionThreads", executionThreads.get());
		log.info("Server config saved!");
	}

	void setShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			log.info("Unloading plugins...");
			pluginsManager.unloadAllPlugins();

			log.info("Saving configurations...");
			saveConfig();
			saveBanlist();
			saveWhitelist();

			log.info("Stopping threads...");
			consoleThread.stopNicely();
			networkThread.stopNicely();
			try {
				networkThread.join(500);
			} catch (InterruptedException ex) {
				log.error("Interrupted while waiting for the network thread to terminate.", ex);
				networkThread.stop();
			} finally {
				LoggingService.close();
			}
			log.info("Stopped.");
		}));
	}

	void startThreads() {
		log.info("Starting threads...");
		consoleThread.start();
		networkThread.start();
		log.info("Threads started!");
	}

	@Override
	public void unregisterWorld(World w) {
		worlds.remove(w.getName());
	}

}
