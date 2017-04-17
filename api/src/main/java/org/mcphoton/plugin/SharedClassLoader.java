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
package org.mcphoton.plugin;

/**
 * Interface that provides convenient methods to share classes between plugins.
 *
 * @see {@link ClassSharer}
 * @author TheElectronWill
 */
public interface SharedClassLoader {

	/**
	 * Tries to find a class.
	 *
	 * @param name the full class name, for instance "java.lang.String"
	 * @param checkShared true to use the sharer to find the class, false to ignore it.
	 * @return the class
	 * @throws ClassNotFoundException if the class can't be found.
	 */
	Class<?> findClass(String name, boolean checkShared) throws ClassNotFoundException;

	/**
	 * Gets the ClassSharer.
	 */
	ClassSharer getSharer();

	/**
	 * Gets the use count. When the use count reach zero, the SharedClassLoader can be removed from the
	 * ClassSharer.
	 */
	int getUseCount();

	/**
	 * Increases and get the use count. When the use count reach zero, the SharedClassLoader can be removed
	 * from the ClassSharer.
	 *
	 * @return the count value, after the increase.
	 */
	int increaseUseCount();

	/**
	 * Decreases and get the use count. When the use count reach zero, the SharedClassLoader can be removed
	 * from the ClassSharer.
	 *
	 * @return the count value, after the decrease.
	 */
	int decreaseUseCount();

}
