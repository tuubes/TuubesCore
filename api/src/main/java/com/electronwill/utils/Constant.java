package com.electronwill.utils;

/**
 * A constant <b>non-null</b> value. Once its value has been set it cannot be changed. A constant is
 * thread-safe.
 *
 * @author TheElectronWill
 */
public final class Constant<T> {
	private volatile T value;

	/**
	 * Creates a non initialized constant.
	 */
	public Constant() {
		// not initialized
	}

	/**
	 * Creates an initialized constant.
	 */
	public Constant(T value) {
		this.value = value;// initialized
	}

	/**
	 * Initializes this constant. This method can only be called once.
	 *
	 * @param value the value to set.
	 */
	public synchronized void init(T value) {
		if (this.value != null) {
			throw new IllegalStateException("Constant already initialized!");
		}
		this.value = value;
	}

	/**
	 * Gets the value.
	 *
	 * @return the constant value.
	 */
	public T get() {
		return value;
	}

	/**
	 * Checks if this constant has been initialized.
	 *
	 * @return true if it has been initialized.
	 */
	public boolean isInitialized() {
		return value != null;
	}
}