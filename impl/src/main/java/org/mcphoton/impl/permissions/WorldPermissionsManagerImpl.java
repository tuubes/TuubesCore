/*
 * Copyright (c) 2017 MCPhoton <http://mcphoton.org> and contributors.
 *
 * This file is part of the Photon Server Implementation <https://github.com/mcphoton/Photon-Server>.
 *
 * The Photon Server Implementation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Photon Server Implementation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mcphoton.impl.permissions;

import java.util.Collection;
import java.util.Collections;
import org.mcphoton.permissions.Permissible;
import org.mcphoton.permissions.PermissionGroup;
import org.mcphoton.permissions.WorldPermissionsManager;

/**
 * @author TheElectronWill
 */
public class WorldPermissionsManagerImpl implements WorldPermissionsManager {
	@Override
	public boolean hasPermission(Permissible p, String perm, boolean ifNotSet) {
		return true;//TODO
	}

	@Override
	public boolean isPermissionSet(Permissible p, String perm) {
		return true;//TODO
	}

	@Override
	public boolean isPermissionSetLocally(Permissible p, String perm) {
		return false;//TODO
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