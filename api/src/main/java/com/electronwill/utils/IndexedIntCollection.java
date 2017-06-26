package com.electronwill.utils;

import java.util.Arrays;
import java.util.Iterator;

/**
 * An indexed collection of primitive integers.
 * <p>
 * The IndexedIntCollection is intended as an internal tool that is used with care, that's why it
 * lacks many bound checks and provides direct access to the array that contains the values.
 *
 * @author TheElectronWill
 */
public abstract class IndexedIntCollection implements Cloneable, Iterable<Integer>, Compactable {
	/**
	 * Contains the collection's values.
	 */
	protected int[] values;
	/**
	 * The collection's size.
	 */
	protected int size = 0;

	/**
	 * Create a new collection with an initial capacity of 10.
	 */
	public IndexedIntCollection() {
		this(10);
	}

	/**
	 * Creates a new collection with the sppecified initial capacity.
	 *
	 * @param initialCapacity the initial number of elements that this collection can contain
	 */
	public IndexedIntCollection(int initialCapacity) {
		this.values = new int[initialCapacity];
	}

	/**
	 * Creates a new collection with the specified intial values. The array is used as it is, it
	 * isn't copied. The collection will still grow when needed.
	 *
	 * @param initialValues the initial values in the collection.
	 */
	protected IndexedIntCollection(int[] initialValues) {
		this.values = initialValues;
		this.size = initialValues.length;
	}

	/**
	 * @return the element at the specified index.
	 */
	public int get(int index) {
		return values[index];
	}

	/**
	 * Sets the value at the specified index.
	 *
	 * @param index the index
	 * @param value the new value to set
	 */
	public void set(int index, int value) {
		values[index] = value;
	}

	/**
	 * Adds a value at the end of this collection.
	 *
	 * @param value the value to add
	 */
	public void add(int value) {
		if (values.length < size + 1) {
			values = Arrays.copyOf(values, values.length * 3 / 2 + 1);
		}
		values[size++] = value;
	}

	/**
	 * Adds several values at the end of this collection.
	 */
	public void addAll(IndexedIntCollection collection) {
		addAll(collection.values);
	}

	/**
	 * Adds several values at the end of this collection.
	 */
	public void addAll(IndexedIntCollection collection, int offset, int length) {
		addAll(collection.values, offset, length);
	}

	/**
	 * Adds several values at the end of this collection.
	 */
	public void addAll(int[] array) {
		addAll(array, 0, array.length);
	}

	/**
	 * Adds several values at the end of this collection.
	 */
	public void addAll(int[] array, int offset, int length) {
		if (values.length < size + length) {
			values = Arrays.copyOf(values, Math.max(values.length * 3 / 2, values.length + length));
		}
		System.arraycopy(array, offset, values, size, length);
		size += length;
	}

	/**
	 * Removes and returns the value at the given index.
	 *
	 * @param index the index of the value to remove
	 * @return the value that was here before its removal
	 */
	public abstract int remove(int index);

	/**
	 * Returns the index of the first occurence of a given value.
	 *
	 * @param value the value to search
	 * @return the index of the first occurence of the value
	 */
	public int indexOf(int value) {
		for (int i = 0; i < size; i++) {
			if (values[i] == value) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Checks if the collection contains the given value at least once.
	 *
	 * @param value the value to search
	 * @return {@code true} iff it contains the value
	 */
	public boolean contains(int value) {
		return indexOf(value) != -1;
	}

	/**
	 * @return the collection's size
	 */
	public int size() {
		return size;
	}

	/**
	 * @return {@code true} iff the collection contains no element
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Sets the collection's size to zero. The actual size in memory remains the same, ie the
	 * underlying array isn't compacted nor deleted.
	 */
	public void clear() {
		size = 0;
	}

	@Override
	public void compact() {
		if (values.length > size) {
			values = Arrays.copyOf(values, size);
		}
	}

	/**
	 * Gets the underlying array that stores the collection's values. Any change to the array is
	 * reflected to the collection, <b>until</b> the collection grows (or is compacted), at which
	 * time the  underlying array is replaced by a new array.
	 *
	 * @return the underlying array that contains the values
	 */
	public int[] values() {
		return values;
	}

	/**
	 * Copies the collection's values to an array.
	 *
	 * @return an array containing a copy of the collection
	 */
	public int[] toArray() {
		return Arrays.copyOf(values, size);
	}

	@Override
	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {
			private int pos = 0;

			@Override
			public boolean hasNext() {
				return pos < size;
			}

			@Override
			public Integer next() {
				return values[pos++];
			}

			@Override
			public void remove() {
				IndexedIntCollection.this.remove(pos);
			}
		};
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getSimpleName()).append(" of size ").append(size).append(" : [");
		for (int i = 0; i < size; i++) {
			sb.append(i);
			sb.append(',');
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(']');
		return sb.toString();
	}

	@Override
	public int hashCode() {
		int result = 1;
		for (int i = 0; i < size; i++) {
			result = 27 * result + values[i];
		}
		return result;
	}

	/**
	 * Checks if this collection is equal to another. An IndexedIntCollection is equal to an
	 * object o if and only if:
	 * <ul>
	 * <li>The object o is an IndexedIntCollection.</li>
	 * <li>And o contains exactly the same values (in the same order) as this collection.</li>
	 * </ul>
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) { return true; }
		if (!(obj instanceof IndexedIntCollection)) { return false; }
		return Arrays.equals(values, ((IndexedIntCollection)obj).values);
	}

	/**
	 * Creates a copy of this collection. The returned object must be an IndexedIntCollection
	 * that contains the same elements (in the same order) as this collection.
	 *
	 * @return a copy of this collection
	 */
	@Override
	public abstract IndexedIntCollection clone();
}