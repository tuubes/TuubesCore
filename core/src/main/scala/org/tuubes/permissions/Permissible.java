package org.tuubes.permissions;

/**
 * Interface for entities or objects that can have permissions.
 */
public interface Permissible {
	/**
	 * Checks if this permissible has the given permission.
	 *
	 * @param permission the permission to check.
	 * @param ifNotSet   the value to return if this permission is unset for this permissible.
	 * @return {@code true} if the permission is granted, {@code false} if it is denied.
	 */
	boolean hasPermission(String permission, boolean ifNotSet);

	/**
	 * Checks if the given permission is set for this Permissible.
	 *
	 * @param permission the permission.
	 * @return {@code true} if it's set, {@code false} if it isn't.
	 */
	boolean isPermissionSet(String permission);
}