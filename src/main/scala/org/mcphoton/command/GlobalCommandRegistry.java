package org.mcphoton.command;

import org.mcphoton.plugin.GlobalPlugin;

/**
 * Registers commands for the global plugins.
 *
 * @author TheElectronWill
 * @see GlobalPlugin
 */
public interface GlobalCommandRegistry {
	/**
	 * Registers a command. The command is registered in every world where the GlobalPlugin is
	 * loaded.
	 *
	 * @param cmd    the command to register.
	 * @param plugin the plugin that registers the command.
	 */
	void registerCommand(Command cmd, GlobalPlugin plugin);

	/**
	 * Unregisters a command.
	 *
	 * @param cmd    the command to unregister.
	 * @param plugin the plugin that previously registered the command.
	 */
	void unregisterCommand(Command cmd, GlobalPlugin plugin);
}