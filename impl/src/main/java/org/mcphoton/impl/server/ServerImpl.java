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

import com.electronwill.utils.SimpleBag;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.mcphoton.Photon;
import org.mcphoton.entity.living.Player;
import org.mcphoton.impl.command.GlobalCommandRegistryImpl;
import org.mcphoton.impl.command.ListCommand;
import org.mcphoton.impl.command.StopCommand;
import org.mcphoton.impl.plugin.GlobalPluginsManagerImpl;
import org.mcphoton.impl.world.WorldImpl;
import org.mcphoton.server.BansManager;
import org.mcphoton.server.Server;
import org.mcphoton.server.ServerConfiguration;
import org.mcphoton.server.WhitelistManager;
import org.mcphoton.world.World;
import org.mcphoton.world.WorldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The game server.
 *
 * @author TheElectronWill
 */
public final class ServerImpl implements Server {
	private static final Logger log = LoggerFactory.getLogger(ServerImpl.class);

	private final ConsoleThread consoleThread = new ConsoleThread();
	private final GlobalPluginsManagerImpl pluginsManager = new GlobalPluginsManagerImpl();
	private final GlobalCommandRegistryImpl commandRegistry = new GlobalCommandRegistryImpl();
	public final ScheduledExecutorService executorService;
	private final BansManagerImpl bansManager = new BansManagerImpl();
	private final WhitelistManagerImpl whitelistManager = new WhitelistManagerImpl();
	private final ServerConfigImpl config = new ServerConfigImpl();

	private final Collection<Player> onlinePlayers = new SimpleBag<>();
	private final Map<String, World> worlds = new ConcurrentHashMap<>();

	public ServerImpl() {
		System.out.println("Initializing...");
		loadWorlds();
		config.load(this);// Intended leak to allow the config to use the worlds map
		executorService = Executors.newScheduledThreadPool(config.getThreadNumber());
		bansManager.load();
		whitelistManager.load();
	}

	@Override
	public String getVersion() {
		return "dev-alpha-08/05/17";
	}

	@Override
	public ServerConfiguration getConfiguration() {
		return config;
	}

	@Override
	public BansManager getBansManager() {
		return bansManager;
	}

	@Override
	public WhitelistManager getWhitelistManager() {
		return whitelistManager;
	}

	public ConsoleThread getConsoleThread() {
		return consoleThread;
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
	public World getWorld(String name) {
		return worlds.get(name);
	}

	@Override
	public Collection<World> getWorlds() {
		return Collections.unmodifiableCollection(worlds.values());
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
		log.info("Loading worlds infos...");
		if (!Photon.WORLDS_DIR.isDirectory()) {
			Photon.WORLDS_DIR.mkdir();
		}
		for (File worldDir : Photon.WORLDS_DIR.listFiles(File::isDirectory)) {
			World world = new WorldImpl(worldDir, WorldType.OVERWORLD);
			worlds.put(world.getName(), world);
		}
		log.info("Found {} worlds.", worlds.size());
	}

	void registerCommands() {
		log.info("Registering photon commands...");
		commandRegistry.registerInternalCommand(new StopCommand());
		commandRegistry.registerInternalCommand(new ListCommand());
	}

	void setShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			log.info("Unloading plugins...");
			pluginsManager.unloadAllPlugins();

			log.info("Saving configurations...");
			config.save();
			bansManager.save();
			whitelistManager.save();

			log.info("Stopping threads...");
			consoleThread.stopNicely();
			//TODO networkThread.stopNicely();
			/*
			try {
				networkThread.join(500);
			} catch (InterruptedException ex) {
				log.error("Interrupted while waiting for the network thread to terminate.", ex);
				networkThread.stop();
			} finally {
				LoggingService.close();
			}
			*/
			log.info("Stopped.");
		}));
	}

	void startThreads() {
		log.info("Starting threads...");
		consoleThread.start();
		// TODO with ProtocolLib: networkThread.start();
		log.info("Threads started!");
	}
}