/*
 * Copyright (c) 2017 MCPhoton <http://mcphoton.org> and contributors.
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

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingException;
import com.electronwill.nightconfig.toml.TomlConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;
import org.mcphoton.Photon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for blacklist and whitelist managers, to avoid duplicated code.
 *
 * @author TheElectronWill
 */
abstract class BWListManager {
	protected static final ConfigSpec CONFIG_SPEC = new ConfigSpec();

	static {
		CONFIG_SPEC.define("accounts", Collections.emptyList());
		CONFIG_SPEC.define("ips", Collections.emptyList());
	}

	protected final String name;
	protected final File file;
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected final Set<UUID> accountSet = new ConcurrentSkipListSet<>();
	protected final Set<InetAddress> addressSet = new ConcurrentSkipListSet<>();

	BWListManager(String name) {
		this.name = name;
		this.file = new File(Photon.getMainDirectory(), name + ".toml");
	}

	void load() {
		try {
			boolean created = file.createNewFile();
			if (created) {// The file didn't exist before => Fill it with the basic structure
				Writer writer = new FileWriter(file);
				writer.write("accounts = []" + System.lineSeparator() + "ips = []");
				writer.close();
				return;// No need to parse the file
			}
		} catch (IOException | SecurityException e) {
			throw new RuntimeException("Failed to create the " + name + " file", e);
		}
		TomlConfig config;
		try {
			config = new TomlParser().parse(file);
		} catch (ParsingException e) {
			throw new StartupFailedException("Cannor parse the " + name, e);
		}
		int corrected = CONFIG_SPEC.correct(config);
		if (corrected > 0) {
			logger.info("Corrected {} entries in {}.", corrected, name);
			save();
		}
		if (!config.isEmpty()) {
			List<?> accounts = config.getValue("accounts");
			for (Object account : accounts) {
				try {
					UUID playerId = UUID.fromString((String)account);
					accountSet.add(playerId);
				} catch (IllegalArgumentException | ClassCastException e) {
					logger.warn("Invalid account id in {}: {}", name, account);
				}
			}
			List<?> ips = config.getValue("ips");
			for (Object ip : ips) {
				try {
					InetAddress address = InetAddress.getByName((String)ip);
					addressSet.add(address);
				} catch (IllegalArgumentException | UnknownHostException | ClassCastException e) {
					logger.warn("Invalid ip address in {}: {}", name, ip);
				}
			}
		}
	}

	void save() {
		TomlConfig config = new TomlConfig();
		config.setValue("accounts", accountSet);
		config.setValue("ips", addressSet);
		try {
			config.write(file);
		} catch (WritingException e) {
			logger.error("Cannot save the " + name, e);
		}
	}
}