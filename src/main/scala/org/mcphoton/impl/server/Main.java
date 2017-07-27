package org.mcphoton.impl.server;

import org.mcphoton.Photon;

/**
 * The main class which launches the program.
 *
 * @author TheElectronWill
 */
public final class Main {
	public static void main(String[] args) {
		ServerImpl server = Photon.getServer();
		server.start();
	}
}