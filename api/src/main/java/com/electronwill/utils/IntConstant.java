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
 * A constant <b>positive</b> integer. Once its value has been set it cannot be changed. An IntConstant is
 * thread-safe.
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
			throw new IllegalArgumentException("IntConstant cannot be initialized with a value of -1");
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
