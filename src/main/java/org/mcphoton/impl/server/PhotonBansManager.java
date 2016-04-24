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

import java.net.InetAddress;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;
import org.mcphoton.server.BansManager;

/**
 *
 * @author TheElectronWill
 */
public class PhotonBansManager implements BansManager {

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

}
