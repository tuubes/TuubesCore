package org.mcphoton.server;

import java.net.InetAddress;
import java.util.Collection;
import java.util.UUID;

/**
 * Common interface for blacklist and whitelist.
 *
 * @author TheElectronWill
 */
public interface AccessList {
	boolean contains(UUID accountId);

	boolean contains(InetAddress ip);

	void add(UUID accountId);

	void add(InetAddress ip);

	void remove(UUID accountId);

	void remove(InetAddress ip);

	Collection<UUID> getAccounts();

	Collection<InetAddress> getIPs();
}