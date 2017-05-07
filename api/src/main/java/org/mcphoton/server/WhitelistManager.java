/*
 * Copyright (c) 2016 MCPhoton <http://mcphoton.org> and contributors.
 *
 * This file is part of the Photon API <https://github.com/mcphoton/Photon-API>.
 *
 * The Photon API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Photon API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mcphoton.server;

import java.net.InetAddress;
import java.util.UUID;

/**
 * Manages the whitelist.
 */
public interface WhitelistManager {

	/**
	 * Checks if the whitelist is enabled.
	 *
	 * @return {@code true} if is enabled
	 */
	boolean isEnabled();

	/**
	 * Enables or disables the whitelist.
	 *
	 * @param enabled {@code true} to enable, {@code false} to disable.
	 */
	void setEnabled(boolean enabled);

	/**
	 * Adds an account to the whitelist.
	 *
	 * @param accountId the account id
	 */
	void add(UUID accountId);

	/**
	 * Adds an IP address to the whitelist.
	 *
	 * @param ip the ip address
	 */
	void add(InetAddress ip);

	/**
	 * Removes an account from the whitelist
	 *
	 * @param accountId the account id
	 */
	void remove(UUID accountId);

	/**
	 * Removes an IP address from the whitelist.
	 *
	 * @param ip the ip address
	 */
	void remove(InetAddress ip);

	/**
	 * Checks if an account is in the whitelist.
	 *
	 * @param accountId the account id
	 * @return {@code true} if it's in the whitelist
	 */
	boolean isInWhitelist(UUID accountId);

	/**
	 * Checks if an IP address is in the whitelist.
	 *
	 * @param ip the ip address
	 * @return {@code true} if it's in the whitelist
	 */
	boolean isInWhitelist(InetAddress ip);
}