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