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

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * A resizeable collection based on an array. The delete operation is in constant time because it just moves
 * the last element to fill the gap.
 *
 * @author TheElectronWill
 * @param <E>
 */
public class SimpleBag<E> extends AbstractCollection<E> implements Bag<E> {

	private class BagIterator implements Iterator<E> {

		private int pos = 0;

		@Override
		public boolean hasNext() {
			return pos < size;
		}

		@Override
		public E next() {
			return (E) array[pos++];
		}

		@Override
		public void remove() {
			SimpleBag.this.remove(pos);
		}

	}
	private final int capacityIncrement;
	private Object[] array;
	private int size = 0;

	/**
	 * Constructs a new Bag with an initial capacity of ten and an increment of 2.
	 */
	public SimpleBag() {
		this(10, 2);
	}

	/**
	 * Creates a new SimpleBag with the given initialCapacity and a default increment of 2.
	 *
	 * @param initialCapacity
	 */
	public SimpleBag(int initialCapacity) {
		this(initialCapacity, 2);
	}

	/**
	 * Creates a new SimpleBag with the given initialCapacity and increment.
	 *
	 * @param initialCapacity
	 * @param capacityIncrement
	 */
	public SimpleBag(int initialCapacity, int capacityIncrement) {
		array = new Object[initialCapacity];
		this.capacityIncrement = capacityIncrement;
	}

	public SimpleBag(Object firstElement) {
		this(10, 2);
		array[size++] = firstElement;
	}

	public SimpleBag(Object firstElement, int initialCapacity, int capacityIncrement) {
		this(initialCapacity, capacityIncrement);
		array[size++] = firstElement;
	}

	@Override
	public boolean add(E e) {
		if (size == array.length) {
			array = Arrays.copyOf(array, size + capacityIncrement);
		}
		array[size++] = e;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		for (E o : c) {
			add(o);
		}
		return true;
	}

	@Override
	public void clear() {
		array = new Object[10];
	}

	@Override
	public boolean contains(Object o) {
		for (int i = 0; i < size; i++) {
			Object e = array[i];
			if (e.equals(o)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public E get(int index) {
		if (index < size) {
			return (E) array[index];
		}
		throw new ArrayIndexOutOfBoundsException(index);
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public Iterator<E> iterator() {
		return new BagIterator();
	}

	@Override
	public void remove(int index) {
		array[index] = array[--size];
		array[size] = null;
	}

	@Override
	public boolean remove(Object o) {
		for (int j = 0; j < size; j++) {
			Object element = array[j];
			if (element.equals(o)) {
				remove(j);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		for (Object o : c) {
			remove(o);
		}
		return true;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				remove(o);
			}
		}
		return true;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public Object[] toArray() {
		return Arrays.copyOf(array, size);
	}

	@Override
	public void trimToSize() {
		array = Arrays.copyOf(array, size);
	}

	@Override
	public E tryGet(int index) {
		if (index < size) {
			return (E) array[index];
		}
		return null;
	}

}
