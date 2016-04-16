package org.mcphoton;

import java.io.File;
import org.mcphoton.block.BlockRegistry;
import org.mcphoton.command.CommandsRegistry;
import org.mcphoton.entity.EntityRegistry;
import org.mcphoton.event.EventsManager;
import org.mcphoton.impl.server.Main;
import org.mcphoton.impl.block.PhotonBlockRegistry;
import org.mcphoton.impl.command.PhotonCommandsRegistry;
import org.mcphoton.impl.entity.PhotonEntityRegistry;
import org.mcphoton.impl.event.PhotonEventsManager;
import org.mcphoton.impl.item.PhotonItemRegistry;
import org.mcphoton.impl.plugin.PhotonPluginsManager;
import org.mcphoton.impl.world.PhotonBiomeRegistry;
import org.mcphoton.item.ItemRegistry;
import org.mcphoton.network.PacketsManager;
import org.mcphoton.plugin.PluginsManager;
import org.mcphoton.world.BiomeRegistry;

/**
 * Implementation of the Photon's class, which is the centralized API core.
 *
 * @author TheElectronWill
 */
public final class Photon {

	private static final File MAIN_DIR = new File(System.getProperty("user.dir")), PLUGINS_DIR = new File(MAIN_DIR, "plugins");
	private static final BlockRegistry BLOCK_REGISTRY = new PhotonBlockRegistry();
	private static final ItemRegistry ITEM_REGISTRY = new PhotonItemRegistry();
	private static final EntityRegistry ENTITY_REGISTRY = new PhotonEntityRegistry();
	private static final BiomeRegistry BIOME_REGISTRY = new PhotonBiomeRegistry();
	private static final CommandsRegistry CMD_REGISTRY = new PhotonCommandsRegistry();
	private static final EventsManager EVENTS_MANAGER = new PhotonEventsManager();
	private static final PluginsManager PLUGINS_MANAGER = new PhotonPluginsManager();

	private Photon() {
	}

	public static PacketsManager getPacketsManager() {
		return Main.serverInstance.packetsManager;
	}

	public static PluginsManager getPluginsManager() {
		return PLUGINS_MANAGER;
	}

	public static EventsManager getEventsManager() {
		return EVENTS_MANAGER;
	}

	public static EntityRegistry getEntityRegistry() {
		return ENTITY_REGISTRY;
	}

	public static CommandsRegistry getCommandsRegistry() {
		return CMD_REGISTRY;
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

	public static String getVersion() {
		return "0.3.0-alpha";
	}

	public static String getMinecraftVersion() {
		return "1.9";
	}

	public static File getMainDirectory() {
		return MAIN_DIR;
	}

	public static File getPluginsDirectory() {
		return PLUGINS_DIR;
	}

}
