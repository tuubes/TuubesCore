package org.mcphoton.impl.command;

import org.mcphoton.Photon;
import org.mcphoton.command.Command;
import org.mcphoton.command.GlobalCommandRegistry;
import org.mcphoton.command.WorldCommandRegistry;
import org.mcphoton.plugin.GlobalPlugin;
import org.mcphoton.world.World;

/**
 * Implementation of {@link GlobalCommandRegistry}. It internally uses the {@link
 * WorldCommandRegistry} of each world.
 *
 * @author TheElectronWill
 */
public class GlobalCommandRegistryImpl implements GlobalCommandRegistry {
	/**
	 * Registers a server command.
	 *
	 * @param cmd the command to register.
	 */
	public void registerInternalCommand(Command cmd) {
		for (World world : Photon.getServer().getWorlds()) {
			world.getCommandRegistry().registerCommand(cmd, null);
		}
	}

	@Override
	public void registerCommand(Command cmd, GlobalPlugin plugin) {
		for (World world : plugin.getActiveWorlds()) {
			world.getCommandRegistry().registerCommand(cmd, plugin);
		}
	}

	@Override
	public void unregisterCommand(Command cmd, GlobalPlugin plugin) {
		for (World world : plugin.getActiveWorlds()) {
			world.getCommandRegistry().unregisterCommand(cmd, plugin);
		}
	}
}