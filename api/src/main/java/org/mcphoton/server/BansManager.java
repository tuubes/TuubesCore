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
import java.util.Collection;
import java.util.UUID;

/**
 * Manages banned accounts and IPs.
 */
public interface BansManager {

	/**
	 * Checks if an account is banned from the server.
	 *
	 * @param accountId the account id
	 * @return {@code true} if it is banned
	 */
	boolean isBanned(UUID accountId);

	/**
	 * Checks if an IP address is banned from the server.
	 *
	 * @param ip the ip address
	 * @return {@code true} if it is banned
	 */
	boolean isBanned(InetAddress ip);

	/**
	 * Bans an account from the server.
	 *
	 * @param accountId the account id
	 */
	void ban(UUID accountId);

	/**
	 * Bans an IP address from the server.
	 *
	 * @param ip the ip address
	 */
	void ban(InetAddress ip);

	/**
	 * Unbans an account from the server.
	 *
	 * @param accountId the account id
	 */
	void unban(UUID accountId);

	/**
	 * Unbans an IP address from the server.
	 *
	 * @param ip the ip address
	 */
	void unban(InetAddress ip);

	/**
	 * Gets all the banned accounts.
	 *
	 * @return an unmodifiable collection containing all the banned accounts.
	 */
	Collection<UUID> getBannedAccounts();

	/**
	 * Gets all the banned IP addresses.
	 *
	 * @return an unmodifiable collection containing all the banned addresses.
	 */
	Collection<InetAddress> getBannedIPs();
}