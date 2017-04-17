/*
 * Copyright (c) 2016 MCPhoton <http://mcphoton.org> and contributors.
 *
 * This file is part of the Photon API <https://github.com/mcphoton/Photon-API>.
 *
 * The Photon API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Photon API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mcphoton;

import java.io.File;
import java.util.concurrent.ScheduledExecutorService;
import org.mcphoton.permissions.PermissionsManager;
import org.mcphoton.server.Server;

/**
 * The centralized API core. Gives access to many core points of the API, like the {@link PermissionsManager}.
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
	 * <li>Musn't be IO-bound, in order to avoid delaying the other tasks. Use an asynchronous IO API
	 * instead of the ExecutorService.</li>
	 * <li>Musn't be too short, in order to avoid creating too much overhead. It is advised to group many
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
	 * Gets the Photon's PermissionsManager, which handles players' permissions.
	 *
	 * @return the PermissionsManager
	 */
	public static PermissionsManager getPermissionsManager() {
		return null;
	}

	/**
	 * Gets the Photon's GameRegistry, which handles the registration of biomes, blocks, items and entities.
	 *
	 * @return the GameRegistry
	 */
	public static GameRegistry getGameRegistry() {
		return null;
	}

	/**
	 * @return true if we're client side.
	 */
	public static boolean isClient() {
		return false;
	}

	/**
	 * @return true if we're server side.
	 */
	public static boolean isServer() {
		return false;
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
	public static String getVersion() {
		return "dev-alpha";
	}

	/**
	 * @return the Minecraft version the API works with.
	 */
	public static String getMinecraftVersion() {
		return "1.10";
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
	 * @return the Server instance (if server-side).
	 */
	public static Server getServer() {
		return null;
	}

}
