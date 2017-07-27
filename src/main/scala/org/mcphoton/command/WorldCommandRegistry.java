package org.mcphoton.command;

import java.util.List;
import org.mcphoton.plugin.Plugin;

/**
 * Registers commands for one world.
 *
 * @author TheElectronWill
 */
public interface WorldCommandRegistry {
	/**
	 * Registers a command.
	 *
	 * @param cmd    the command to register.
	 * @param plugin the plugin that registers the command.
	 */
	void registerCommand(Command cmd, Plugin plugin);

	/**
	 * Unregister a command.
	 *
	 * @param cmd    the command to unregister.
	 * @param plugin the plugin that previously registered the command.
	 */
	void unregisterCommand(Command cmd, Plugin plugin);

	/**
	 * Gets a registered command by its name.
	 *
	 * @param cmdName the command's name, or the name of an alias.
	 */
	Command getRegisteredCommand(String cmdName);

	/**
	 * Gets a list of all the commands registered by a plugin.
	 */
	List<Command> getRegisteredCommands(Plugin plugin);
}