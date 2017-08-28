package org.mcphoton.server;

import com.electronwill.utils.StringUtils;
import java.util.List;
import java.util.Scanner;
import org.mcphoton.Photon;
import org.mcphoton.messaging.ChatMessage;
import org.mcphoton.messaging.Messageable;
import org.mcphoton.world.World;

/**
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
			List<String> parts = StringUtils.split(line, ' ');
			Command cmd = world.getCommandRegistry().getRegisteredCommand(parts.get(0));
			if (cmd == null) {
				System.out.println("Unknown command.");
				continue;
			}
			String[] array = new String[parts.size() - 1];
			if (array.length == 0) {
				cmd.execute(this, new String[0]);
			} else {
				cmd.execute(this, parts.subList(1, parts.size() - 1).toArray(array));
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