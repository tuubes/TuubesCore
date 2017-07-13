package com.electronwill.utils;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * A resizeable collection based on an array. The delete operation is in constant time because it
 * just moves the last element to fill the gap.
 *
 * @author TheElectronWill
 */
public class SimpleBag<E> extends AbstractCollection<E> implements Bag<E> {
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
	 */
	public SimpleBag(int initialCapacity) {
		this(initialCapacity, 2);
	}

	/**
	 * Creates a new SimpleBag with the given initialCapacity and increment.
	 */
	public SimpleBag(int initialCapacity, int capacityIncrement) {
		array = new Object[initialCapacity];
		this.capacityIncrement = capacityIncrement;
	}

	public SimpleBag(E firstElement) {
		this(10, 2);
		array[size++] = firstElement;
	}

	public SimpleBag(E firstElement, int initialCapacity, int capacityIncrement) {
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
			return (E)array[index];
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
	public E remove(int index) {
		E element = (E)array[index];
		array[index] = array[--size];
		array[size] = null;
		return element;
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
	public void compact() {
		array = Arrays.copyOf(array, size);
	}

	@Override
	public E tryGet(int index) {
		if (index < size) {
			return (E)array[index];
		}
		return null;
	}

	private class BagIterator implements Iterator<E> {
		private int pos = 0;

		@Override
		public boolean hasNext() {
			return pos < size;
		}

		@Override
		public E next() {
			return (E)array[pos++];
		}

		@Override
		public void remove() {
			SimpleBag.this.remove(pos);
		}
	}
}