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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;
import org.mcphoton.Photon;
import org.mcphoton.server.WhitelistManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author TheElectronWill
 */
public final class WhitelistManagerImpl implements WhitelistManager {

	private static final Logger log = LoggerFactory.getLogger(WhitelistManagerImpl.class);
	private static final WhitelistManagerImpl INSTANCE = new WhitelistManagerImpl();
	private static final File FILE = new File(Photon.MAIN_DIR, "whitelist.txt");

	public static WhitelistManagerImpl getInstance() {
		return INSTANCE;
	}

	private WhitelistManagerImpl() {
	}

	private final Set<UUID> whitelist = new ConcurrentSkipListSet<>();
	private volatile boolean enabled;

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void allow(UUID accountId) {
		whitelist.add(accountId);
	}

	@Override
	public void deny(UUID accountId) {
		whitelist.remove(accountId);
	}

	@Override
	public boolean isAllowed(UUID accountId) {
		return whitelist.contains(accountId);
	}

	public void load() throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(FILE), StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				try {
					UUID playerId = UUID.fromString(line);
					whitelist.add(playerId);
				} catch (IllegalArgumentException ex) {
					log.error("Invalid UUID in whitelist.", ex);
				}
			}
		}
		log.info("Whitelist loaded.");
	}

	public void save() throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FILE), StandardCharsets.UTF_8))) {
			for (UUID uuid : whitelist) {
				writer.write(uuid.toString());
				writer.newLine();
			}
		}
		log.info("Whitelist saved.");
	}

}
