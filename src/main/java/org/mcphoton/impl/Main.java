package org.mcphoton.impl;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import org.mcphoton.Photon;

/**
 * Classe de lancement du programme.
 *
 * @author TheElectronWill
 *
 */
public class Main {

	static final String os = System.getProperty("os.name");

	public static void main(String[] args) {
		printFramed("Photon server version " + Photon.getVersion(), "For minecraft version " + Photon.getMinecraftVersion());
		try {
			KeyPair rsaKeyPair = generateRsaKeyPair();
			PhotonServer server = new PhotonServer(rsaKeyPair);
			server.specifyConfig();
			server.reloadConfig();
			server.setShutdownHook();
			server.startThreads();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static KeyPair generateRsaKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(512);
		return generator.genKeyPair();
	}

	private static void printFramed(String... strings) {
		int max = 0;
		for (String s : strings) {
			if (s.length() > max) {
				max = s.length();
			}
		}

		for (int i = 0; i < max + 4; i++) {
			System.out.print('-');
		}
		System.out.println();

		for (String s : strings) {
			System.out.print("| ");
			System.out.print(s);
			for (int i = 0; i < max - s.length(); i++) {
				System.out.print(' ');
			}
			System.out.println(" |");
		}

		for (int i = 0; i < max + 4; i++) {
			System.out.print('-');
		}
		System.out.println();
	}

}
