package com.electronwill.utils;

/**
 * A list of primitive integers.
 *
 * @author TheElectronWill
 */
public final class IntList extends IndexedIntCollection {
	/**
	 * Creates a new IntList with an initial capacity of 10.
	 */
	public IntList() {
		super();
	}

	/**
	 * Creates a new IntList with the given initial capacity.
	 */
	public IntList(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Creates a new IntList with the specified intial values. The array is used as it is, it isn't
	 * copied. The list will still grow when needed.
	 */
	public IntList(int[] initialValues) {
		super(initialValues);
	}

	public int remove(int index) {
		// Keeps the order of the elements
		int element = values[index];
		int lastIndex = size - 1;
		if (index != lastIndex) {
			System.arraycopy(values, index + 1, values, index, lastIndex - index);
		}
		size--;
		return element;
	}

	@Override
	public IntList clone() {
		return new IntList(toArray());
	}
}