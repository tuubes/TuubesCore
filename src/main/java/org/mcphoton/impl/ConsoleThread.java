package org.mcphoton.impl;

import java.util.Scanner;

/**
 *
 * @author TheElectronWill
 */
public class ConsoleThread extends Thread {

	private volatile boolean run = true;
	private final Scanner sc = new Scanner(System.in);

	/**
	 * Stops this Thread nicely, as soon as possible but without any forcing.
	 */
	public void stopNicely() {
		run = false;
	}

	@Override
	public void run() {
		while (run) {

		}
	}

}
