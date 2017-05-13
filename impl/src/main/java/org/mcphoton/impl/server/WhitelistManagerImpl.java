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