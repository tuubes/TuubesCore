package org.mcphoton.server;

import com.electronwill.utils.SimpleBag;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import org.mcphoton.Photon;
import org.mcphoton.entity.living.Player;
import org.mcphoton.command.StopCommand;
import org.mcphoton.network.ProtocolLibAdapter;
import org.mcphoton.plugin.GlobalPluginsManagerImpl;
import org.mcphoton.world.WorldImpl;
import org.mcphoton.world.World;
import org.mcphoton.world.WorldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.PhotonLogger;

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
	private final AccessListImpl whitelist = new AccessListImpl("whitelist");
	private final AccessListImpl blackList = new AccessListImpl("blacklist");
	private final ServerConfigImpl config = new ServerConfigImpl();
	private final ProtocolLibAdapter libAdapter;
	public final ScheduledExecutorService executorService;

	private final Collection<Player> onlinePlayers = new SimpleBag<>();
	private final Map<String, World> worlds = new ConcurrentHashMap<>();

	public ServerImpl() {
		log.info("Photon Server "
				 + Photon.getImplVersion()
				 + ", API "
				 + Photon.getAPIVersion()
				 + ", for MC "
				 + Photon.getMinecraftVersion());
		loadWorlds();
		config.load(this);// Intended leak to allow the config to use the worlds map
		PhotonLogger.setLevel(config.getLogLevel());
		libAdapter = new ProtocolLibAdapter(config.getPort());
		executorService = new ErrorAwareScheduledExecutor(config.getThreadNumber());
	}

	@Override
	public ServerConfiguration getConfiguration() {
		return config;
	}

	@Override
	public AccessList getWhitelist() {
		return whitelist;
	}

	@Override
	public AccessList getBlacklist() {
		return blackList;
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

	void start() {
		whitelist.load();
		blackList.load();
		loadPlugins();
		registerCommands();
		consoleThread.start();
		libAdapter.start();
		setShutdownHook();
		log.info("Startup completed!");
	}

	private void loadPlugins() {
		log.info("Loading plugins...");
		if (!Photon.getPluginsDirectory().isDirectory()) {
			Photon.getPluginsDirectory().mkdir();
		}
		try {
			pluginsManager.loadAllPlugins();
		} catch (IOException ex) {
			log.error("Unexpected error while trying to load the plugins.", ex);
		}
	}

	private void loadWorlds() {
		if (!Photon.getWorldsDirectory().isDirectory()) {
			Photon.getWorldsDirectory().mkdir();
		}
		for (File worldDir : Photon.getWorldsDirectory().listFiles(File::isDirectory)) {
			World world = new WorldImpl(worldDir, WorldType.OVERWORLD);
			worlds.put(world.getName(), world);
		}
		log.info("Found {} worlds.", worlds.size());
	}

	private void registerCommands() {
		log.info("Registering server commands...");
		commandRegistry.registerInternalCommand(new StopCommand());
		commandRegistry.registerInternalCommand(new ListCommand());
	}

	private void setShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			log.info("Unloading plugins...");
			pluginsManager.unloadAllPlugins();

			log.info("Saving configurations...");
			config.save();
			whitelist.save();
			blackList.save();

			log.info("Stopping threads...");
			consoleThread.stopNicely();
			libAdapter.stop();
			log.info("Photon Server stopped.");
		}));
	}
}