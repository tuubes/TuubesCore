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
package org.mcphoton;

import java.io.File;
import java.util.concurrent.ScheduledExecutorService;
import org.mcphoton.block.BlockRegistry;
import org.mcphoton.entity.EntityRegistry;
import org.mcphoton.impl.block.BlockRegistryImpl;
import org.mcphoton.impl.entity.EntityRegistryImpl;
import org.mcphoton.impl.item.ItemRegistryImpl;
import org.mcphoton.impl.server.Main;
import org.mcphoton.impl.world.BiomeRegistryImpl;
import org.mcphoton.item.ItemRegistry;
import org.mcphoton.network.PacketsManager;
import org.mcphoton.server.Server;
import org.mcphoton.world.BiomeRegistry;

/**
 * Implementation of the Photon's class, which is the centralized API core.
 *
 * @author TheElectronWill
 */
public final class Photon {

	public static final File MAIN_DIR = new File(System.getProperty("user.dir")), PLUGINS_DIR = new File(MAIN_DIR, "plugins"), WORLDS_DIR = new File(MAIN_DIR, "worlds");
	public static final File CONFIG_FILE = new File(MAIN_DIR, "server_config.toml"), ICON_PNG = new File(MAIN_DIR, "server_icon.png"), ICON_JPG = new File(MAIN_DIR, "server_icon.png");
	private static final BlockRegistry BLOCK_REGISTRY = new BlockRegistryImpl();
	private static final ItemRegistry ITEM_REGISTRY = new ItemRegistryImpl();
	private static final EntityRegistry ENTITY_REGISTRY = new EntityRegistryImpl();
	private static final BiomeRegistry BIOME_REGISTRY = new BiomeRegistryImpl();
	private static final boolean consoleAdvanced = !System.getProperty("os.name").toLowerCase().contains("windows");

	private Photon() {
	}

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
	 */
	public static ScheduledExecutorService getExecutorService() {
		return Main.EXECUTOR_SERVICE;
	}

	public static PacketsManager getPacketsManager() {
		return Main.SERVER.packetsManager;
	}

	public static EntityRegistry getEntityRegistry() {
		return ENTITY_REGISTRY;
	}

	public static BlockRegistry getBlockRegistry() {
		return BLOCK_REGISTRY;
	}

	public static ItemRegistry getItemRegistry() {
		return ITEM_REGISTRY;
	}

	public static BiomeRegistry getBiomeRegistry() {
		return BIOME_REGISTRY;
	}

	public static boolean isClient() {
		return false;
	}

	public static boolean isServer() {
		return true;
	}

	public static boolean isConsoleAdvanced() {
		return consoleAdvanced;
	}

	public static String getVersion() {
		return "dev-alpha";
	}

	public static String getMinecraftVersion() {
		return "1.10";
	}

	public static File getMainDirectory() {
		return MAIN_DIR;
	}

	public static File getPluginsDirectory() {
		return PLUGINS_DIR;
	}

	public static Server getServer() {
		return Main.SERVER;
	}
}
