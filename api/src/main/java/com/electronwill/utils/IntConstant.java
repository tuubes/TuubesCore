package com.electronwill.utils;

/**
 * A constant <b>positive</b> integer. Once its value has been set it cannot be changed. An
 * IntConstant is thread-safe.
 *
 * @author TheElectronWill
 */
public final class IntConstant {
	private volatile int value;

	/**
	 * Creates a non initialized constant.
	 */
	public IntConstant() {
		this.value = -1;// not initialized
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
		if (value == -1) {
			throw new IllegalArgumentException(
					"IntConstant cannot be initialized with a value of -1");
		}
		synchronized (this) {
			if (this.value != -1) {
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
		return value != -1;
	}
}