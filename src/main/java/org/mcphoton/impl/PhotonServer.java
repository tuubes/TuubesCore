package org.mcphoton.impl;

import com.electronwill.utils.SimpleBag;
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.mcphoton.entity.living.player.Player;
import org.mcphoton.impl.network.NetworkInputThread;
import org.mcphoton.impl.network.NetworkOutputThread;
import org.mcphoton.impl.network.PhotonPacketsManager;
import org.mcphoton.network.PacketsManager;
import org.mcphoton.server.BansManager;
import org.mcphoton.server.Server;
import org.mcphoton.server.WhitelistManager;
import org.mcphoton.world.Location;
import org.mcphoton.world.World;
import org.slf4j.impl.LoggingService;
import org.slf4j.impl.PhotonLogger;

/**
 * The game server.
 *
 * @author TheElectronWill
 *
 */
public final class PhotonServer implements Server {

	public final PhotonLogger logger;
	public final KeyPair keyPair;
	public final InetSocketAddress address;
	public final NetworkInputThread networkInputThread;
	public final NetworkOutputThread networkOutputThread;
	public final ConsoleThread consoleThread = new ConsoleThread();
	public final PacketsManager packetsManager = new PhotonPacketsManager(this);

	public volatile String motd;

	public final Collection<Player> onlinePlayers = new SimpleBag<>();
	public volatile int maxPlayers;

	public final Map<String, World> worlds = new HashMap<>();
	public volatile Location spawn;

	public PhotonServer(PhotonLogger logger, KeyPair keyPair, InetSocketAddress address, NetworkInputThread networkInputThread, NetworkOutputThread networkOutputThread, String motd, int maxPlayers, Location spawn) {
		this.logger = logger;
		this.keyPair = keyPair;
		this.address = address;
		this.networkInputThread = networkInputThread;
		this.networkOutputThread = networkOutputThread;
		this.motd = motd;
		this.maxPlayers = maxPlayers;
		this.spawn = spawn;
	}

	void startThreads() {
		consoleThread.start();
		networkInputThread.start();
		networkOutputThread.start();
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
				logger.error("Unable to close the network threads in an acceptable time", ex);
				logger.warn("The network threads will be forcibly stopped!");
				networkInputThread.stop();
				networkOutputThread.stop();
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
