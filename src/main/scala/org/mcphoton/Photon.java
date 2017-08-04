package org.mcphoton;

import java.io.File;
import java.util.concurrent.ScheduledExecutorService;
import org.mcphoton.command.GlobalCommandRegistry;
import org.mcphoton.event.GlobalEventsManager;
import org.mcphoton.command.GlobalCommandRegistryImpl;
import org.mcphoton.event.GlobalEventsManagerImpl;
import org.mcphoton.permissions.GlobalPermissionsManagerImpl;
import org.mcphoton.plugin.GlobalPluginsManagerImpl;
import org.mcphoton.server.ServerImpl;
import org.mcphoton.permissions.GlobalPermissionsManager;
import org.mcphoton.plugin.GlobalPluginsManager;

/**
 * Implementation of the Photon's class, which is the centralized API core.
 *
 * @author TheElectronWill
 */
public final class Photon {

	private static final boolean CONSOLE_ADVANCED = !System.getProperty("os.name").toLowerCase()
														   .contains("windows");
	private static final File MAIN_DIR = new File(System.getProperty("user.dir")),
			PLUGINS_DIR = new File(MAIN_DIR, "plugins"),
			WORLDS_DIR = new File(MAIN_DIR, "worlds");
	private static final GameRegistry GAME_REGISTRY = new GameRegistry();
	private static final GlobalCommandRegistry COMMAND_REGISTRY = new GlobalCommandRegistryImpl();
	private static final GlobalPluginsManager PLUGINS_MANAGER = new GlobalPluginsManagerImpl();
	private static final GlobalEventsManager EVENTS_MANAGER = new GlobalEventsManagerImpl();
	private static final GlobalPermissionsManager PERM_MANAGER = new GlobalPermissionsManagerImpl();
	private static final ServerImpl SERVER_INSTANCE = new ServerImpl();

	private Photon() {}

	/**
	 * Gets the Photon's ScheduledExecutorService, which is used to schedule tasks across multiple
	 * threads.
	 * <h2>What kind of task may be submitted to this ExecutorService?</h2>
	 * <p>
	 * To achieve better performance, the submitted tasks:
	 * <ol>
	 * <li>Musn't be IO-bound, in order to avoid delaying the other tasks. Use an asynchronous IO
	 * API instead of the ExecutorService.</li>
	 * <li>Musn't be too short, in order to avoid creating too much overhead. It is advised to group
	 * many small tasks together into one bigger task.</li>
	 * </ol>
	 * </p>
	 */
	public static ScheduledExecutorService getExecutorService() {
		return SERVER_INSTANCE.executorService;
	}

	public static GlobalPermissionsManager getGlobalPermissionsManager() {
		return PERM_MANAGER;
	}

	public static GlobalPluginsManager getGlobalPluginsManager() {
		return PLUGINS_MANAGER;
	}

	public static GlobalCommandRegistry getGlobalCommandRegistry() {
		return COMMAND_REGISTRY;
	}

	public static GlobalEventsManager getGlobalEventsManager() {
		return EVENTS_MANAGER;
	}

	public static GameRegistry getGameRegistry() {
		return GAME_REGISTRY;
	}

	public static boolean isConsoleAdvanced() {
		return CONSOLE_ADVANCED;
	}

	public static String getAPIVersion() {
		return "dev-alpha";
	}

	public static String getImplVersion() {
		return "dev-alpha";
	}

	public static String getMinecraftVersion() {
		return "1.11";
	}

	public static File getMainDirectory() {
		return MAIN_DIR;
	}

	public static File getPluginsDirectory() {
		return PLUGINS_DIR;
	}

	public static File getWorldsDirectory() {
		return WORLDS_DIR;
	}

	public static ServerImpl getServer() {
		return SERVER_INSTANCE;
	}
}