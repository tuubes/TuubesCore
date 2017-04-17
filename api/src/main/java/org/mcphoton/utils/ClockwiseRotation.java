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
package org.mcphoton.utils;

/**
 * A clockwise rotation, in steps of 45 degrees.
 *
 * @author TheElectronWill
 */
public enum ClockwiseRotation {

	CLOCKWISE_0, CLOCKWISE_45,
	CLOCKWISE_90, CLOCKWISE_135,
	CLOCKWISE_180, CLOCKWISE_225,
	CLOCKWISE_270, CLOCKWISE_315;
	
	/**
	 * Gets the ClockwiseRotation that corresponds to the specified number of clockwise 45-degrees rotations, starting from 0.
	 * @param t45 the number of 45-degrees rotations to do.
	 */
	public static ClockwiseRotation get(int t45){
		return ClockwiseRotation.values()[t45 & 7];
	}

	/**
	 * Gets the rotation's id (between 0 and 7).
	 */
	public int getId() {
		return ordinal();
	}

	/**
	 * Turn clockwise starting from this rotation.
	 * @param t45 the number of 45-degrees rotation steps to do.
	 */
	public ClockwiseRotation turnClockwise(int t45) {
		return ClockwiseRotation.values()[(ordinal() + t45) & 7];
	}
	
	/**
	 * Turn counter-clockwise starting from this rotation.
	 * @param t45 the number of 45-degrees rotation steps to do.
	 */
	public ClockwiseRotation turnCounterClockwise(int t45) {
		return ClockwiseRotation.values()[(ordinal() - t45) & 7];
	}

}
