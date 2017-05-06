/*
 * Copyright (c) 2016 MCPhoton <http://mcphoton.org> and contributors.
 *
 * This file is part of the Photon Server Implementation <https://github.com/mcphoton/Photon-Server>.
 *
 * The Photon Server Implementation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Photon Server Implementation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mcphoton.impl.server;

import java.util.Scanner;
import org.mcphoton.Photon;
import org.mcphoton.command.Command;
import org.mcphoton.messaging.ChatMessage;
import org.mcphoton.messaging.Messageable;
import org.mcphoton.world.World;

/**
 *
 * @author TheElectronWill
 */
public class ConsoleThread extends Thread implements Messageable {

	/**
	 * The world currently used for the command registry.
	 */
	public volatile World world;

	private volatile boolean run = true;
	private final Scanner sc = new Scanner(System.in);

	public ConsoleThread() {
		super("console");
	}

	/**
	 * Stops this Thread nicely, as soon as possible but without any forcing.
	 */
	public void stopNicely() {
		run = false;
	}

	@Override
	public void run() {
		world = Photon.getServer().getConfiguration().getSpawnLocation().getWorld();
		while (run) {
			String line = sc.nextLine();
			String[] parts = line.split(" ", 2);
			Command cmd = world.getCommandRegistry().getRegisteredCommand(parts[0]);
			if (cmd == null) {
				System.out.println("Unknown command.");
				continue;
			}
			if (parts.length == 1) {
				cmd.execute(this, new String[0]);
			} else {
				cmd.execute(this, parts[1]);
			}
		}
	}

	@Override
	public void sendMessage(CharSequence msg) {
		System.out.println(msg);
	}

	@Override
	public void sendMessage(ChatMessage msg) {
		System.out.println(msg.toConsoleString());
	}

}
