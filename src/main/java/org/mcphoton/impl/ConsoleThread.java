package org.mcphoton.impl;

import java.util.Scanner;
import org.mcphoton.Photon;
import org.mcphoton.command.Command;
import org.mcphoton.messaging.ChatMessage;
import org.mcphoton.messaging.Messageable;

/**
 *
 * @author TheElectronWill
 */
public class ConsoleThread extends Thread implements Messageable {

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
		while (run) {
			String line = sc.nextLine();
			String[] parts = line.split(" ", 2);
			Command cmd = Photon.getCommandsRegistry().getRegistered(parts[0]);
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
