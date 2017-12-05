package com.electronwill.collections;

import java.util.Arrays;
import java.util.Iterator;

/**
 * A thread-safe Bag of elements.
 *
 * @author TheElectronWill
 */
public final class ConcurrentBag<E> extends AbstractBag<E> implements Bag<E> {
	private volatile Object[] array;
	private volatile int size;
	private final int capacityIncrement;

	public ConcurrentBag() {
		this(10, 50);
	}

	public ConcurrentBag(int initialCapacity) {
		this(initialCapacity, 50);
	}

	public ConcurrentBag(int initialCapacity, int capacityIncrement) {
		this.array = new Object[initialCapacity];
		this.capacityIncrement = capacityIncrement;
	}

	@Override
	public E get(int index) {
		final Object[] arr = array;// volatile read
		return (E)arr[index];
	}

	@Override
	public E tryGet(int index) {
		final Object[] arr = array;// volatile read
		return (index >= 0 && index < arr.length) ? (E)arr[index] : null;
	}

	@Override
	public E remove(int index) {
		synchronized (this) {
			Object[] arr = array;
			if (index < 0 || index >= arr.length) {
				return null;
			}
			E element = (E)arr[index];
			if (element != null) {
				int s = size;
				arr[index] = arr[--s];
				arr[s] = null;
				size = s;
			}
			return element;
		}
	}

	@Override
	public void compact() {
		synchronized (this) {
			array = Arrays.copyOf(array, size);
		}
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean contains(Object o) {
		Object[] arr = array;
		int s = Math.min(size, arr.length);
		for (int i = 0; i < s; i++) {
			Object e = arr[i];
			if (e.equals(o)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterator<E> iterator() {
		return new ConcurrentBagIterator();
	}

	@Override
	public Object[] toArray() {
		final Object[] arr = array;
		return Arrays.copyOf(arr, Math.min(size, arr.length));
		//The array could have grown between the volatile get and copyOf
	}

	@Override
	public boolean add(E e) {
		synchronized (this) {
			if (size == array.length) {
				array = Arrays.copyOf(array, size + capacityIncrement);
			}
			array[size++] = e;
			return true;
		}
	}

	@Override
	public boolean remove(Object o) {
		synchronized (this) {
			final int s = size;
			final Object[] arr = array;
			for (int i = 0; i < s; i++) {
				if (arr[i].equals(o)) {
					remove(i);
					return true;
				}
			}
			return false;
		}
	}

	@Override
	public void clear() {
		synchronized (this) {
			array = new Object[10];
			size = 0;
		}
	}

	/**
	 * A weakly consistent entry iterator. It is not guaranteed to reflect the latest state of the
	 * bag but will never throw ConcurrentModificationException.
	 */
	private class ConcurrentBagIterator implements Iterator<E> {
		private final Object[] array;//The array at the time the iterator is created
		private int pos = 0;//The next position
		private E returnedElement;//The last element returned by next

		private ConcurrentBagIterator() {
			this.array = ConcurrentBag.this.array;
		}

		@Override
		public boolean hasNext() {
			return pos < Math.min(size, array.length);
		}

		@Override
		public E next() {
			returnedElement = (E)array[pos++];
			return returnedElement;
		}

		@Override
		public void remove() {
			if (returnedElement == null) {
				throw new IllegalStateException("remove() already called for this element");
			}
			synchronized (ConcurrentBag.this) {
				if (array != ConcurrentBag.this.array) {// The array has changed
					ConcurrentBag.this.remove(returnedElement);// Removes by object
				} else {
					ConcurrentBag.this.remove(--pos);// Removes by index
				}
			}
			returnedElement = null;
		}
	}
}