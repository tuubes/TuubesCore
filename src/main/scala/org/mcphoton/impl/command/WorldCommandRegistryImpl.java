package org.mcphoton.impl.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.mcphoton.command.Command;
import org.mcphoton.command.WorldCommandRegistry;
import org.mcphoton.plugin.Plugin;

/**
 * Implementation of {@link WorldCommandRegistry}.
 *
 * @author TheElectronWill
 */
public class WorldCommandRegistryImpl implements WorldCommandRegistry {
	private final Map<String, Command> nameMap = new HashMap<>();
	private final Map<Plugin, List<Command>> pluginMap = new HashMap<>();

	@Override
	public synchronized void registerCommand(Command cmd, Plugin plugin) {
		nameMap.put(cmd.getName(), cmd);
		List<Command> list = pluginMap.get(plugin);
		if (list == null) {
			list = new LinkedList<>();
			pluginMap.put(plugin, list);
		}
		list.add(cmd);
		for (String alias : cmd.getAliases()) {
			nameMap.putIfAbsent(alias, cmd);
		}
	}

	@Override
	public synchronized void unregisterCommand(Command cmd, Plugin plugin) {
		nameMap.remove(cmd.getName(), cmd);
		List<Command> list = pluginMap.get(plugin);
		if (list != null) {
			list.remove(cmd);
		}
		for (String alias : cmd.getAliases()) {
			nameMap.remove(alias, cmd);
		}
	}

	@Override
	public synchronized Command getRegisteredCommand(String cmdName) {
		return nameMap.get(cmdName);
	}

	@Override
	public synchronized List<Command> getRegisteredCommands(Plugin plugin) {
		List list = pluginMap.get(plugin);
		return list == null ? null : Collections.unmodifiableList(list);
	}
}