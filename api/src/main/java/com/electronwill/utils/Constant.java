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
