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
package com.electronwill.utils;

import java.util.Collection;

/**
 * A resizeable collection based on an array. The delete operation is in constant time because it just moves
 * the last element to fill the gap.
 *
 * @author TheElectronWill
 * @param <E>
 */
public interface Bag<E> extends Collection<E> {

	/**
	 * @param index the element's index.
	 * @return the element at the specified index.
	 * @throws ArrayIndexOutOfBoundsException if the specified index is negative or greater than the size of
	 * the bag.
	 */
	E get(int index);

	/**
	 * @param index the element's index.
	 * @return the element at the specified index, or null if the index is negative or greater than the size
	 * of the bag.
	 */
	E tryGet(int index);

	/**
	 * Removes the element at the specified index, and moves the last element to this index to "fill the gap".
	 *
	 * @param index the element's index.
	 */
	void remove(int index);

	/**
	 * Trims the capacity of this Bag instance to be the bag's current size. Use this operation to minimize
	 * the storage of an Bag instance.
	 */
	void trimToSize();

}
