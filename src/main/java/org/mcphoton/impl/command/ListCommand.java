package org.mcphoton.impl.command;

import org.mcphoton.command.Command;
import org.mcphoton.impl.server.Main;
import org.mcphoton.messaging.Messageable;

public class ListCommand implements Command {
	

	@Override
	public void execute(Messageable source, String[] args) {
		
		switch(Main.serverInstance.getOnlinePlayers().size()) {
		case 0:
			source.sendMessage("No player is connected.");
			break;
		case 1:
			source.sendMessage("There is 1 of " + Main.serverInstance.getMaxPlayers() + " player connected.");
			break;
		default:
			source.sendMessage("There are " + Main.serverInstance.getOnlinePlayers().size() + " of " + Main.serverInstance.getMaxPlayers() + " players connected.");
			break;
		}
	}

	@Override
	public String getName() {
		return "list";
	}

}
