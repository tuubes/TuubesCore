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
package org.mcphoton.permissions;

import java.util.Collection;

/**
 * A GlobalPermissionsManager manages the permissions at a global level.
 * <p>
 * The global permissions are applied by default in every world, and are overriden by specific world
 * settings. For instance, let A be a permission, W be a world, and P be a permissible. Let A be globally
 * set for P (that is, A was granted or denied to P via the {@code GlobalPermissionsManager} or via a
 * global group).
 * <ul>
 * <li>If W has no specific setting about A for P, then he global setting applies.</li>
 * <li>If W has a specific setting about A for P, then this specific setting applies instead of the global
 * one.</li>
 * </ul>
 * Specific world settings are managed by the {@link WorldPermissionsManager}s. Each world has a
 * {@link WorldPermissionsManager} which manages its specific permission settings.
 * </p>
 *
 * @author TheElectronWill
 * @see WorldPermissionsManager
 */
public interface GlobalPermissionsManager {

	/**
	 * Checks if a given Permissible object <b>globally</b> has the specified permission.
	 *
	 * @param p        the permissible.
	 * @param perm     the permission to check.
	 * @param ifNotSet the value to return if the permission is undefined for the given permissible.
	 * @return {@code true} if the permission is granted, {@code false} if it is denied.
	 */
	boolean hasPermission(Permissible p, String perm, boolean ifNotSet);

	/**
	 * Checks if a given permission is <b>globally</b> set on a Permissible.
	 *
	 * @param p    the permissible.
	 * @param perm the permission.
	 * @return {@code true} if the permission is set, {@code false} if it isn't.
	 */
	boolean isPermissionSet(Permissible p, String perm);

	/**
	 * <b>Globally</b> sets a permission for a Permissible.
	 *
	 * @param p       the permissible.
	 * @param perm    the permission.
	 * @param setting {@code true} to grant the permission, {@code false} to deny it.
	 */
	void setPermission(Permissible p, String perm, boolean setting);

	/**
	 * <b>Globally</b> unsets a permission.
	 *
	 * @param p    the permissible.
	 * @param perm the permission to unset.
	 */
	void unsetPermission(Permissible p, String perm);

	/**
	 * Creates a new empty, <b>global</b> PermissionGroup.
	 *
	 * @param name the group's name.
	 * @return a new empty PermissionGroup.
	 */
	PermissionGroup createGroup(String name);

	/**
	 * Deletes a <b>global</b> permission group. The group's member don't get deleted, they are just removed
	 * from the group.
	 *
	 * @param group the group to delete.
	 */
	void deleteGroup(PermissionGroup group);

	/**
	 * Gets a <b>global</b> permission group by its name.
	 *
	 * @param name the group's name.
	 * @return the permission group with that name, or null if there is none.
	 */
	PermissionGroup getGroup(String name);

	/**
	 * Gets a collection of all the existing <b>global</b> permission groups. The returned collection isn't
	 * modifiable.
	 *
	 * @return the existing permission groups.
	 */
	Collection<PermissionGroup> getGroups();
}
