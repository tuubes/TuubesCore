package org.mcphoton.utils;

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

import java.util.Objects;
import org.mcphoton.world.World;

/**
 * An immutable Location defined by its coordinates (x,y,z) and its world.
 *
 * @author TheElectronWill
 */
public final class Location {
	private final double x, y, z;
	private final World w;

	public Location(double x, double y, double z, World w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	@Override
	public Location clone() {
		return new Location(x, y, z, w);
	}

	/**
	 * Creates a new DoubleVector with the coordinates of this location.
	 *
	 * @return a new DoubleVector with the same coordinates as this location.
	 */
	public Vector toVector() {
		return new Vector(x, y, z);
	}

	/**
	 * Creates a new location that is the result of adding the vector v to this location.
	 */
	public Location add(Vector v) {
		return new Location(x + v.x, y + v.y, z + v.z, w);
	}

	/**
	 * Creates a new location that is the result of adding the location l to this location.
	 */
	public Location add(Location l) {
		return new Location(x + l.x, y + l.y, z + l.z, w);
	}

	/**
	 * Creates a new location that is the result of adding the specified numbers to this location.
	 */
	public Location add(double dx, double dy, double dz) {
		return new Location(x + dx, y + dy, z + dz, w);
	}

	public World getWorld() {
		return w;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public int getBlockX() {
		return (int)x;
	}

	public int getBlockY() {
		return (int)y;
	}

	public int getBlockZ() {
		return (int)z;
	}

	public Location withX(double x) {
		return new Location(x, y, z, w);
	}

	public Location withY(double y) {
		return new Location(x, y, z, w);
	}

	public Location withZ(double z) {
		return new Location(x, y, z, w);
	}

	public Location withWorld(World w) {
		return new Location(x, y, z, w);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + Double.hashCode(x);
		hash = 31 * hash + Double.hashCode(y);
		hash = 31 * hash + Double.hashCode(z);
		hash = 31 * hash + Objects.hashCode(this.w);
		return hash;
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + "," + z + ", " + w + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Location) {
			Location l = (Location)obj;
			return w == l.w && x == l.x && y == l.y && z == l.z;
		}
		return false;
	}

	/**
	 * Creates a location in the middle of the two specified locations. The world will be the one of
	 * {@code l1}.
	 */
	public static Location middle(Location l1, Location l2) {
		double x = (l1.x + l2.x) / 2.0;
		double y = (l1.y + l2.y) / 2.0;
		double z = (l1.z + l2.z) / 2.0;
		return new Location(x, y, z, l1.getWorld());
	}

	/**
	 * Calculates the squared distance between this location and the l location. This is faster than
	 * manually multiplying the distance by itself.
	 *
	 * @return the squared distance between this location and l.
	 */
	public double squaredDistance(Location l) {
		double deltaX = l.x - x;
		double deltaY = l.y - y;
		double deltaZ = l.z - z;
		return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
	}

	/**
	 * Calculates the distance between this location and the l location. If you want the squared
	 * distance use {@link #squaredDistance(org.mcphoton.utils.Location)}.
	 *
	 * @return the distance between this location and l.
	 */
	public double distance(Location l) {
		return Math.sqrt(squaredDistance(l));
	}
}