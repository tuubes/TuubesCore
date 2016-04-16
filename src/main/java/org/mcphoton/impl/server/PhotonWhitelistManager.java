package org.mcphoton.impl.server;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;
import org.mcphoton.server.WhitelistManager;

/**
 *
 * @author TheElectronWill
 */
public class PhotonWhitelistManager implements WhitelistManager {

	private final Set<UUID> whitelist = new ConcurrentSkipListSet<>();
	private volatile boolean enabled = true;

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public void allow(UUID accountId) {
		whitelist.add(accountId);
	}

	@Override
	public void deny(UUID accountId) {
		whitelist.remove(accountId);
	}

	@Override
	public boolean isAllowed(UUID accountId) {
		return whitelist.contains(accountId);
	}

}
