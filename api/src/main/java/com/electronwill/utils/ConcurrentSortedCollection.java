package com.electronwill.utils;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

/**
 * A thread-safe collection that accepts duplicates and sorts its elements in ascending order,
 * according to the Comparator provided in the constructor.
 * <p>
 * The collection uses binary search to stay sorted when elements are added.
 * <ul>
 * <li>add is O(log n) and uses binary search to maintain proper ordering</li>
 * <li>remove is O(n) and shifts the elements to maintain proper ordering (like ArrayList)</li>
 * <li>contains is O(log n) in best case and O(n) in worst case where all the elements are
 * comparatively equal, ie comparator.compare(e1,e2) is 0 for all e1,e2 in the collection.</li>
 * </ul>
 *
 * @author TheElectronWill
 */
public final class ConcurrentSortedCollection<E> extends AbstractCollection<E>
		implements Cloneable, Compactable {
	/**
	 * The array that contains the values.
	 */
	private volatile E[] array;

	/**
	 * The number of values in the list.
	 */
	private volatile int size;

	/**
	 * The comparator used to sort the collection.
	 */
	private final Comparator<? super E> comparator;

	public ConcurrentSortedCollection(Comparator<? super E> comparator) {
		this(comparator, 10);
	}

	public ConcurrentSortedCollection(Comparator<? super E> comparator, int initialCapacity) {
		this.comparator = comparator;
		this.array = (E[])new Object[initialCapacity];
	}

	public ConcurrentSortedCollection(Comparator<? super E> comparator, E... initialValues) {
		this.comparator = comparator;
		this.array = initialValues.clone();
		Arrays.sort(array, comparator);
	}

	private ConcurrentSortedCollection(E[] array, Comparator<? super E> comparator) {
		this.comparator = comparator;
		this.array = array;
		this.size = array.length;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void clear() {
		synchronized (this) {
			E[] arr = array;
			Arrays.fill(arr, null);
			array = arr;
			size = 0;
		}
	}

	@Override
	public void compact() {
		synchronized (this) {
			if (array.length != size) {
				array = Arrays.copyOf(array, size);
			}
		}
	}

	@Override
	public boolean contains(Object o) {
		E[] arr = array;
		int s = Math.min(size, arr.length);
		return indexOf(o, arr, s) != -1;
	}

	@Override
	public boolean add(E e) {
		synchronized (this) {
			final E[] arr = array;
			final int s = size, newSize = s + 1;
			ensureCapacity(newSize);
			int index = Arrays.binarySearch(arr, 0, s, e, comparator);
			if (index < 0) {// There is no element with the same "value" as e
				index = (-index - 1);// Gets the insertion point
			}
			System.arraycopy(arr, index, arr, index + 1, s - index);// Shifts the elements
			arr[index] = e;// Puts e at the freed index
			array = arr;
			size = newSize;
		}
		return true;
	}

	private void ensureCapacity(int minCapacity) {
		int currentCapacity = array.length;
		if (minCapacity > currentCapacity) {
			array = Arrays.copyOf(array, currentCapacity + currentCapacity >> 1);//capacity*1.5
		}
	}

	@Override
	public boolean remove(Object o) {
		synchronized (this) {
			E[] arr = array;
			int s = size;
			int index = indexOf(o, arr, s);
			if (index != -1) {
				int moveCount = s - index - 1;
				if (moveCount > 0) {
					System.arraycopy(arr, index + 1, arr, index, moveCount);
				}
				array = arr;
				size = s - 1;
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the index of the given object. The returned index isn't necessarily the one of the
	 * first element that is equal to o, the only guarantee is that the returned index is such
	 * that arr[index] is equal to o.
	 *
	 * @param o   the object to search
	 * @param arr the array containing the collection's elements
	 * @param s   the number of valid elements in the array
	 * @return the index of the object, or -1 if not found
	 */
	private int indexOf(Object o, E[] arr, int s) {
		final E other;
		try {
			other = (E)o;
		} catch (ClassCastException e) {
			return -1;
		}
		// Gets an index of an element that satisfies comparator.compare(e,o) == 0
		int index = Arrays.binarySearch(arr, 0, s, other, comparator);
		if (index < 0) {
			return -1;
		}
		/* We want to find the element e such that 'e.equals(o)' is true.
		Assuming that 'e.equals(o)' implies 'comparator.compare(e,o) == 0' (Note that this is an
		implication, not an equivalence, that's why the index obtained by binary search isn't
		immediately returned), we only need to search among the elements such that 'comparator
		.compare(e,o) == 0', not the entire	array.
		*/
		E e;
		// Search in range [index, lastElementComparativelyEqual]:
		for (int i = index; i < s && (comparator.compare((e = arr[i]), other) == 0); i++) {
			if (e.equals(other)) {
				return i;
			}
		}
		// Search in range [firstElementComparativelyEqual, index-1]:
		for (int i = index - 1; i >= 0 && (comparator.compare((e = arr[i]), other) == 0); i--) {
			if (e.equals(other)) {
				return i;
			}
		}
		return -1;// Not found
	}

	@Override
	public Object[] toArray() {
		E[] arr = array;
		return Arrays.copyOf(arr, Math.min(size, arr.length));
		//The array could have grown between the volatile get and copyOf
	}

	@Override
	protected ConcurrentSortedCollection<E> clone() {
		synchronized (this) {
			E[] copy = Arrays.copyOf(array, size);
			return new ConcurrentSortedCollection<E>(copy, comparator);
		}
	}

	/**
	 * Returns a weakly-consistent iterator.
	 */
	@Override
	public Iterator<E> iterator() {
		return new ConcurrentListIterator();
	}

	/**
	 * A weakly consistent entry iterator. It is not guaranteed to reflect the latest state of the
	 * map but will never throw ConcurrentModificationException.
	 */
	private class ConcurrentListIterator implements Iterator<E> {
		private final E[] array;//The array at the time the iterator is created
		private int pos = 0;
		private E returnedElement;//The last element returned by next

		private ConcurrentListIterator() {
			this.array = ConcurrentSortedCollection.this.array;
		}

		@Override
		public boolean hasNext() {
			return pos < Math.min(size, array.length);
		}

		@Override
		public E next() {
			returnedElement = array[pos++];
			return returnedElement;
		}

		@Override
		public void remove() {
			if (returnedElement == null) {
				throw new IllegalStateException("remove() may only be called once per next()");
			}
			synchronized (ConcurrentSortedCollection.this) {
				if (array != ConcurrentSortedCollection.this.array) {// The array has changed
					ConcurrentSortedCollection.this.remove(returnedElement);// Removes by object
				} else {
					ConcurrentSortedCollection.this.remove(pos);// Removes by index
				}
			}
			returnedElement = null;
		}
	}
}
