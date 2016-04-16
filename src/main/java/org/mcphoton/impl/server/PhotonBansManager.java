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
