package org.mcphoton.command;

import org.mcphoton.Photon;
import org.mcphoton.server.ConsoleThread;
import org.mcphoton.messaging.Messageable;
import org.mcphoton.world.World;

/**
 * Changes the world where the commands are executed. Similar to the "cd" command, but for worlds.
 *
 * @author TheElectronWill
 */
public class ChangeWorldCommand implements Command {
	private static final String[] ALIASES = {"cw"};

	@Override
	public void execute(Messageable source, String[] args) {
		if (args.length != 1) {
			source.sendMessage("Invalid syntax. Use cw [world]");
			return;
		}
		World world = Photon.getServer().getWorld(args[0]);
		if (world == null) {
			source.sendMessage("This world does not exist.");
			return;
		}
		if (source instanceof ConsoleThread) {
			Photon.getServer().getConsoleThread().world = world;
		} else {
			source.sendMessage("This command only works in the server's console!");
		}
	}

	@Override
	public String getDescription() {
		return "Changes the world where the console commands are executed. Only works in the server's console.";
	}

	@Override
	public String getName() {
		return "change-world";
	}

	@Override
	public String[] getAliases() {
		return ALIASES;
	}
}