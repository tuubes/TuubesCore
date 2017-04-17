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

/**
 * A Permissible is something that can have permissions.
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
