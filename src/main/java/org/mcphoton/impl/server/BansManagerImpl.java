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

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;
import org.mcphoton.Photon;
import org.mcphoton.config.TomlConfiguration;
import org.mcphoton.server.BansManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author TheElectronWill
 */
public final class BansManagerImpl implements BansManager {

	private static final Logger log = LoggerFactory.getLogger(WhitelistManagerImpl.class);
	private static final BansManagerImpl INSTANCE = new BansManagerImpl();
	private static final File FILE = new File(Photon.MAIN_DIR, "bans.toml");

	public static BansManagerImpl getInstance() {
		return INSTANCE;
	}

	private BansManagerImpl() {
	}

	private final Set<UUID> bannedAccounts = new ConcurrentSkipListSet<>();
	private final Set<InetAddress> bannedAddresses = new ConcurrentSkipListSet<>();

	@Override
	public boolean isBanned(UUID accountId) {
		return bannedAccounts.contains(accountId);
	}

	@Override
	public boolean isBanned(InetAddress ip) {
		return bannedAddresses.contains(ip);
	}

	@Override
	public void ban(UUID accountId) {
		bannedAccounts.add(accountId);
	}

	@Override
	public void ban(InetAddress ip) {
		bannedAddresses.add(ip);
	}

	@Override
	public void unban(UUID accountId) {
		bannedAccounts.remove(accountId);
	}

	@Override
	public void unban(InetAddress ip) {
		bannedAddresses.remove(ip);
	}

	@Override
	public Collection<UUID> getBannedAccounts() {
		return bannedAccounts;
	}

	@Override
	public Collection<InetAddress> getBannedIPs() {
		return bannedAddresses;
	}

	public void load() throws IOException {
		TomlConfiguration config = new TomlConfiguration(FILE);
		if (config.isEmpty()) {
			return;
		}

		List<String> accounts = (List) config.get("accounts");
		for (String account : accounts) {
			try {
				UUID playerId = UUID.fromString(account);
				bannedAccounts.add(playerId);
			} catch (IllegalArgumentException ex) {
				log.error("Invalid UUID in ban list.", ex);
			}
		}

		List<String> ips = (List) config.get("ips");
		for (String ip : ips) {
			try {
				InetAddress address = InetAddress.getByName(ip);
				bannedAddresses.add(address);
			} catch (IllegalArgumentException ex) {
				log.error("Invalid IP address in ban list.", ex);
			}
		}
		log.info("Ban list loaded.");
	}

	public void save() throws IOException {
		TomlConfiguration config = new TomlConfiguration();
		config.put("ips", bannedAddresses);
		config.put("accounts", bannedAccounts);
		config.writeTo(FILE);
		log.info("Ban list saved.");
	}
}
