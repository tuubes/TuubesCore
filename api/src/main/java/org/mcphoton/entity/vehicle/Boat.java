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
package org.mcphoton.entity.vehicle;

/**
 * A boat.
 *
 * @author TheElectronWill
 */
public interface Boat extends Vehicle {

	/**
	 * @return true if the boat is turning with its right paddle.
	 */
	boolean isTurningRight();

	/**
	 * Sets if the boat turns right.
	 *
	 * @param turning true if the boat is turning with its right paddle.
	 */
	void setTurningRight(boolean turning);

	/**
	 * @return true if the boat is turning with its left paddle.
	 */
	boolean isTurningLeft();

	/**
	 * Sets if the boat turns left.
	 *
	 * @param turning true if the boat is turning with its left paddle.
	 */
	void setTurningLeft(boolean turning);
}
