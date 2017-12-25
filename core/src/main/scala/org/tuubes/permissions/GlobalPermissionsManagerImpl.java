package org.tuubes.permissions;

import java.util.Collection;
import java.util.Collections;

/**
 * @author TheElectronWill
 */
public class GlobalPermissionsManagerImpl implements GlobalPermissionsManager {
	@Override
	public boolean hasPermission(Permissible p, String perm, boolean ifNotSet) {
		return true;//TODO
	}

	@Override
	public boolean isPermissionSet(Permissible p, String perm) {
		return true;//TODO
	}

	@Override
	public void setPermission(Permissible p, String perm, boolean setting) {
		//TODO
	}

	@Override
	public void unsetPermission(Permissible p, String perm) {
		//TODO
	}

	@Override
	public PermissionGroup createGroup(String name) {
		return null;//TODO
	}

	@Override
	public void deleteGroup(PermissionGroup group) {
		//TODO
	}

	@Override
	public PermissionGroup getGroup(String name) {
		return null;//TODO
	}

	@Override
	public Collection<PermissionGroup> getGroups() {
		return Collections.emptyList();//TODO
	}
}