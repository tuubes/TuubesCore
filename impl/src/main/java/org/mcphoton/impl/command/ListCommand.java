package org.mcphoton.impl.command;

import org.mcphoton.Photon;
import org.mcphoton.command.Command;
import org.mcphoton.messaging.Messageable;

public class ListCommand implements Command {
	@Override
	public void execute(Messageable source, String[] args) {

		switch (Photon.getServer().getOnlinePlayers().size()) {
			case 0:
				source.sendMessage("No player is connected.");
				break;
			case 1:
				source.sendMessage("There is 1 out of "
								   + Photon.getServer().getConfiguration().getMaxPlayers()
								   + " player connected.");
				break;
			default:
				source.sendMessage("There are "
								   + Photon.getServer().getOnlinePlayers().size()
								   + " out of "
								   + Photon.getServer().getConfiguration().getMaxPlayers()
								   + " players connected.");
				break;
		}
	}

	@Override
	public String getDescription() {
		return "Lists the connected players.";
	}

	@Override
	public String getName() {
		return "list";
	}
}