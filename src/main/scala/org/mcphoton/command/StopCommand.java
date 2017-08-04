package org.mcphoton.command;

import org.mcphoton.command.Command;
import org.mcphoton.messaging.Messageable;

/**
 * @author TheElectronWill
 */
public class StopCommand implements Command {
	@Override
	public void execute(Messageable source, String[] args) {
		System.exit(0);
	}

	@Override
	public String getDescription() {
		return "Stops the server";
	}

	@Override
	public String getName() {
		return "stop";
	}
}