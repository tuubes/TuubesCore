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

import org.mcphoton.world.World;

/**
 * A location is a precise point defined by 3 coordinates (getX(),getY(),getZ()) and one World.
 * location is a "read only" interface intended to be used when you want to give the information
 * (coordinates + world) without the ability to change it.
 *
 * @author TheElectronWill
 */
public interface Location extends Cloneable {

	/**
	 * @return the location's world.
	 */
	World getWorld();

	/**
	 * @return the x coordinate.
	 */
	double getX();

	/**
	 * @return the y coordinate.
	 */
	double getY();

	/**
	 * @return the z coordinate.
	 */
	double getZ();

	/**
	 * @return the x coordinate rounded down.
	 */
	int getBlockX();

	/**
	 * @return the y coordinate rounded down.
	 */
	int getBlockY();

	/**
	 * @return the z coordinate rounded down.
	 */
	int getBlockZ();

	/**
	 * Creates a new location with the specified x coordinate and the same y, z and World as this
	 * location.
	 */
	Location withX(double x);

	/**
	 * Creates a new location with the specified y coordinate and the same x, z and World as this
	 * location.
	 */
	Location withY(double y);

	/**
	 * Creates a new location with the specified z coordinate and the same x, y and World as this
	 * location.
	 */
	Location withZ(double z);

	/**
	 * Creates a new location with the specified world and the same x, y and z as this
	 * location.
	 */
	Location withWorld(World w);

	/**
	 * Creates a new IntVector with the coordinates of this location. Each coordinate is converted to an int.
	 *
	 * @return a new IntVector with the same coordinates as this location.
	 */
	default IntVector toIntVector() {
		return new IntVector(getBlockX(), getBlockY(), getBlockZ());
	}

	/**
	 * Creates a new DoubleVector with the coordinates of this location.
	 *
	 * @return a new DoubleVector with the same coordinates as this location.
	 */
	default DoubleVector toDoubleVector() {
		return new DoubleVector(getX(), getY(), getZ());
	}

	/**
	 * Calculates the squared distance between this location and the l location. This is faster than manually
	 * multiplying the distance by itself.
	 *
	 * @return the squared distance between this location and l.
	 */
	default double squaredDistance(Location l) {
		double deltaX = l.getX() - getX();
		double deltaY = l.getY() - getY();
		double deltaZ = l.getZ() - getZ();
		return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
	}

	/**
	 * Calculates the distance between this location and the l location. If you want the squared distance use
	 * {@link #squaredDistance(org.mcphoton.utils.Location)}.
	 *
	 * @return the distance between this location and l.
	 */
	default double distance(Location l) {
		double deltaX = l.getX() - getX();
		double deltaY = l.getY() - getY();
		double deltaZ = l.getZ() - getZ();
		return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
	}

}
