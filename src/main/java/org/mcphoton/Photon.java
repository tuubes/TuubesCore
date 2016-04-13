package org.mcphoton;

import java.io.File;
import org.mcphoton.block.BlockRegistry;
import org.mcphoton.command.CommandsRegistry;
import org.mcphoton.entity.EntityRegistry;
import org.mcphoton.event.EventsManager;
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

	private Photon() {
	}

	public static PacketsManager getPacketsManager() {
		return null;
	}

	public static PluginsManager getPluginsManager() {
		return null;
	}

	public static EventsManager getEventsManager() {
		return null;
	}

	public static EntityRegistry getEntityRegistry() {
		return null;
	}

	public static CommandsRegistry getCommandsRegistry() {
		return null;
	}

	public static BlockRegistry getBlockRegistry() {
		return null;
	}

	public static ItemRegistry getItemRegistry() {
		return null;
	}

	public static BiomeRegistry getBiomeRegistry() {
		return null;
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
