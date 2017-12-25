package org.tuubes.permissions;

import java.util.Collection;

/**
 * A WorldPermissionsManager manages the permissions at a world level. In the documentation, the
 * "managed world" refers to the world for which the WorldPermissionsManager works.
 * <p>
 * The world permissions override the global permissions.
 * </p>
 *
 * @author TheElectronWill
 * @see GlobalPermissionsManager
 */
public interface WorldPermissionsManager {
	/**
	 * Checks if a given Permissible object has the specified permission in the managed world.
	 * <p>
	 * The result of this method is determined as follows:
	 * <ul>
	 * <li>If the specified permission is set in the managed world, the world setting is
	 * returned.</li>
	 * <li>Else, if the specified permission is set globally, the global setting is returned.</li>
	 * <li>Else the {@code ifNotSet} boolean value is returned.</li>
	 * </ul>
	 * </p>
	 *
	 * @param p        the permissible.
	 * @param perm     the permission to check.
	 * @param ifNotSet the value to return if the permission is undefined for the given
	 *                 permissible.
	 * @return {@code true} if the permission is granted, {@code false} if it is denied.
	 */
	boolean hasPermission(Permissible p, String perm, boolean ifNotSet);

	/**
	 * Checks if a given permission is set on a Permissible, either in the managed world or
	 * globally.
	 *
	 * @param p    the permissible.
	 * @param perm the permission.
	 * @return {@code true} if the permission is set, {@code false} if it isn't.
	 */
	boolean isPermissionSet(Permissible p, String perm);

	/**
	 * Checks if a given permission is set on a Permissible, in the managed world (not globally).
	 *
	 * @param p    the permissible.
	 * @param perm the permission.
	 * @return {@code true} if the permission is set, {@code false} if it isn't.
	 */
	boolean isPermissionSetLocally(Permissible p, String perm);

	/**
	 * Sets a permission for a Permissible, in the managed world.
	 *
	 * @param p       the permissible.
	 * @param perm    the permission.
	 * @param setting {@code true} to grant the permission, {@code false} to deny it.
	 */
	void setPermission(Permissible p, String perm, boolean setting);

	/**
	 * Unsets a permission, in the managed world.
	 *
	 * @param p    the permissible.
	 * @param perm the permission to unset.
	 */
	void unsetPermission(Permissible p, String perm);

	/**
	 * Creates a new empty PermissionGroup, in the managed world.
	 *
	 * @param name the group's name.
	 * @return a new empty PermissionGroup.
	 */
	PermissionGroup createGroup(String name);

	/**
	 * Deletes a PermissionGroup, from the managed world. The group's member don't get deleted, they
	 * are just removed from the group.
	 *
	 * @param group the group to delete.
	 */
	void deleteGroup(PermissionGroup group);

	/**
	 * Gets a permission group (of the managed world) by its name.
	 *
	 * @param name the group's name.
	 * @return the permission group with that name, or null if there is none.
	 */
	PermissionGroup getGroup(String name);

	/**
	 * Gets a collection of all the existing permission groups for the managed world (no global
	 * groups). The returned  collection is unmodifiable.
	 *
	 * @return the existing permission groups.
	 */
	Collection<PermissionGroup> getGroups();
}