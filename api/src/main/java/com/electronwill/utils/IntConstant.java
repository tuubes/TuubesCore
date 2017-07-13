package com.electronwill.utils;

/**
 * A constant integer. Once its value has been set it cannot be changed. An IntConstant is
 * thread-safe. The int value must be different from {@code Integer.MIN_VALUE}.
 *
 * @author TheElectronWill
 */
public final class IntConstant {
	private static final int NON_INITIALIZED = Integer.MIN_VALUE;

	private volatile int value;

	/**
	 * Creates a non initialized constant.
	 */
	public IntConstant() {
		this.value = NON_INITIALIZED;
	}

	/**
	 * Creates an initialized constant.
	 */
	public IntConstant(int value) {
		this.value = value;// initialized
	}

	/**
	 * Initializes the constant. This method can only be called once.
	 *
	 * @param value the value to set.
	 */
	public void init(int value) {
		if (value == NON_INITIALIZED) {
			throw new IllegalArgumentException(
					"IntConstant cannot be initialized with a value of Integer.MIN_VALUE");
		}
		synchronized (this) {
			if (this.value != NON_INITIALIZED) {
				throw new IllegalStateException("IntConstant already initialized!");
			}
			this.value = value;
		}
	}

	/**
	 * Gets the value.
	 *
	 * @return the constant value.
	 */
	public int get() {
		return value;
	}

	/**
	 * Checks if this constant has been initialized.
	 *
	 * @return true if it has been initialized.
	 */
	public boolean isInitialized() {
		return value != NON_INITIALIZED;
	}
}