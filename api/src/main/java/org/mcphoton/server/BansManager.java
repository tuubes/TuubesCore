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