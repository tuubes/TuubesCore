package com.electronwill.utils;

/**
 * A bag of primitive integers.
 *
 * @author TheElectronWill
 */
public final class IntBag extends IndexedIntCollection {
	/**
	 * Creates a new IntBag with an initial capacity of 10.
	 */
	public IntBag() {
		super();
	}

	/**
	 * Creates a new IntBag with the given initial capacity.
	 */
	public IntBag(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Creates a new IntBag with the specified intial values. The array is used as it is, it isn't
	 * copied. The bag will still grow when needed.
	 */
	public IntBag(int[] initialValues) {
		super(initialValues);
	}

	public int remove(int index) {
		// Don't keep the order of the elements: just move the last element to the removed index.
		int element = values[index];
		values[index] = values[--size];
		return element;
	}

	@Override
	public IntBag clone() {
		return new IntBag(toArray());
	}
}