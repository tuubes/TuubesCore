package org.mcphoton.impl.server;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;
import org.mcphoton.server.BansManager;

/**
 * @author TheElectronWill
 */
public final class BansManagerImpl extends BWListManager implements BansManager {
	BansManagerImpl() {
		super("blacklist");
	}

	@Override
	public boolean isBanned(UUID accountId) {
		return accountSet.contains(accountId);
	}

	@Override
	public boolean isBanned(InetAddress ip) {
		return addressSet.contains(ip);
	}

	@Override
	public void ban(UUID accountId) {
		accountSet.add(accountId);
	}

	@Override
	public void ban(InetAddress ip) {
		addressSet.add(ip);
	}

	@Override
	public void unban(UUID accountId) {
		accountSet.remove(accountId);
	}

	@Override
	public void unban(InetAddress ip) {
		addressSet.remove(ip);
	}

	@Override
	public Collection<UUID> getBannedAccounts() {
		return accountSet;
	}

	@Override
	public Collection<InetAddress> getBannedIPs() {
		return addressSet;
	}
}