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
import java.util.UUID;
import org.mcphoton.server.WhitelistManager;

/**
 * @author TheElectronWill
 */
public final class WhitelistManagerImpl extends BWListManager implements WhitelistManager {
	WhitelistManagerImpl() {
		super("whitelist");
	}

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
	public void add(UUID accountId) {
		accountSet.add(accountId);
	}

	@Override
	public void add(InetAddress ip) {
		addressSet.add(ip);
	}

	@Override
	public void remove(UUID accountId) {
		accountSet.remove(accountId);
	}

	@Override
	public void remove(InetAddress ip) {
		addressSet.remove(ip);
	}

	@Override
	public boolean isInWhitelist(UUID accountId) {
		return accountSet.contains(accountId);
	}

	@Override
	public boolean isInWhitelist(InetAddress ip) {
		return addressSet.contains(ip);
	}
}