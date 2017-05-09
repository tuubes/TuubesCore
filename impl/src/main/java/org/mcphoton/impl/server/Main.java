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

	static void printFramed(String... strings) {
		// Determines the maximum length
		int max = 0;
		for (String s : strings) {
			if (s.length() > max) {
				max = s.length();
			}
		}
		// Prints the top bar
		for (int i = 0; i < max + 4; i++) {
			System.out.print('-');
		}
		System.out.println();
		// Prints the strings
		for (String s : strings) {
			System.out.print("| ");
			System.out.print(s);
			for (int i = 0; i < max - s.length(); i++) {
				System.out.print(' ');
			}
			System.out.println(" |");
		}
		// Prints the bottom bar
		for (int i = 0; i < max + 4; i++) {
			System.out.print('-');
		}
		System.out.println();
	}
}