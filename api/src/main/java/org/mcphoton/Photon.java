package org.mcphoton;

import java.io.File;
import java.util.concurrent.ScheduledExecutorService;
import org.mcphoton.command.GlobalCommandRegistry;
import org.mcphoton.event.GlobalEventsManager;
import org.mcphoton.permissions.GlobalPermissionsManager;
import org.mcphoton.plugin.GlobalPluginsManager;
import org.mcphoton.server.Server;

/**
 * The centralized API core. Gives access to many core points of the API.
 *
 * @author TheElectronWill
 */
public final class Photon {
	/*
	 * Every method here returns null and it's completely normal. Actually the photon implementation redefines
	 * this class and completes its methods.
	 */
	private Photon() {}

	/**
	 * Gets the Photon's ScheduledExecutorService, which is used to schedule tasks across multiple
	 * threads.
	 * <h2>What kind of task may be submitted to this ExecutorService?</h2>
	 * <p>
	 * To achieve better performance, the submitted tasks:
	 * <ol>
	 * <li>Musn't be IO-bound, in order to avoid delaying the other tasks. Use an asynchronous IO
	 * API
	 * instead of the ExecutorService.</li>
	 * <li>Musn't be too short, in order to avoid creating too much overhead. It is advised to group
	 * many
	 * small tasks together into one bigger task.</li>
	 * </ol>
	 * </p>
	 *
	 * @return the ScheduledExecutorService
	 */
	public static ScheduledExecutorService getExecutorService() {
		return null;
	}

	/**
	 * @return the GlobalPermissionsManager
	 */
	public static GlobalPermissionsManager getGlobalPermissionsManager() {
		return null;
	}

	/**
	 * @return the GlobalPluginsManager
	 */
	public static GlobalPluginsManager getGlobalPluginsManager() {
		return null;
	}

	/**
	 * @return the GlobalEventsManager
	 */
	public static GlobalEventsManager getGlobalEventsManager() {
		return null;
	}

	/**
	 * @return the GlobalCommandRegistry
	 */
	public static GlobalCommandRegistry getGlobalCommandRegistry() {
		return null;
	}

	/**
	 * @return true if the console is advanced, ie if it can display colors and text effects.
	 */
	public static boolean isConsoleAdvanced() {
		return false;
	}

	/**
	 * @return the API version.
	 */
	public static String getAPIVersion() {
		return "dev-alpha";
	}

	/**
	 * @return the Implementation version.
	 */
	public static String getImplVersion() {
		return "dev-alpha";
	}

	/**
	 * @return the Minecraft version the API works with.
	 */
	public static String getMinecraftVersion() {
		return "1.11";
	}

	/**
	 * @return the main directory.
	 */
	public static File getMainDirectory() {
		return null;
	}

	/**
	 * @return the plugins' directory.
	 */
	public static File getPluginsDirectory() {
		return null;
	}

	/**
	 * @return the worlds' directory.
	 */
	public static File getWorldsDirectory() {
		return null;
	}

	/**
	 * @return the Server instance (if server-side).
	 */
	public static Server getServer() {
		return null;
	}
}