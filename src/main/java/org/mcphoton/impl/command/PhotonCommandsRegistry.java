package org.mcphoton.impl.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.mcphoton.command.Command;
import org.mcphoton.command.CommandsRegistry;
import org.mcphoton.plugin.Plugin;

/**
 *
 * @author TheElectronWill
 */
public class PhotonCommandsRegistry implements CommandsRegistry {

	private final Map<String, Command> nameMap = new HashMap<>();
	private final Map<Plugin, List<Command>> pluginMap = new HashMap<>();

	@Override
	public synchronized void register(Command cmd, Plugin plugin) {
		nameMap.put(cmd.getName(), cmd);
		List<Command> list = pluginMap.get(plugin);
		if (list == null) {
			list = new LinkedList<>();
			pluginMap.put(plugin, list);
		}
		list.add(cmd);
	}

	@Override
	public synchronized void unregister(Command cmd, Plugin plugin) {
		nameMap.remove(cmd.getName(), cmd);
		List<Command> list = pluginMap.get(plugin);
		if (list != null) {
			list.remove(cmd);
		}
	}

	@Override
	public synchronized Command getRegistered(String cmdName) {
		return nameMap.get(cmdName);
	}

	@Override
	public synchronized List<Command> getAllRegistered(Plugin plugin) {
		List list = pluginMap.get(plugin);
		return list == null ? null : Collections.unmodifiableList(list);
	}

}
