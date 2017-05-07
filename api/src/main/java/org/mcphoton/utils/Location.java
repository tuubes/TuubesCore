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
public final class Location implements Coordinates, Cloneable {
	private final double x, y, z;
	private final World w;

	public Location(double x, double y, double z, World w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Location(Coordinates coords, World world) {
		this(coords.getX(), coords.getY(), coords.getZ(), world);
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
	 * Creates a new location by adding some coordinates to this location.
	 */
	public Location add(double dx, double dy, double dz) {
		return new Location(x + dx, y + dy, z + dz, w);
	}

	/**
	 * Creates a new location by adding some coordinates to this location.
	 */
	public Location add(Coordinates coords) {
		return add(coords.getX(), coords.getY(), coords.getZ());
	}

	/**
	 * Creates a new location by substracting some coordinates to this location.
	 */
	public Location sub(double dx, double dy, double dz) {
		return new Location(x - dx, y - dy, z - dz, w);
	}

	/**
	 * Creates a new location by substracting some coordinates to this location.
	 */
	public Location sub(Coordinates coords) {
		return sub(coords.getX(), coords.getY(), coords.getZ());
	}

	public World getWorld() {
		return w;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
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
	 * Creates a location in the middle of two locations. The world will be the one of {@code l1}.
	 *
	 * @param l1 the first location
	 * @param l2 the second location
	 */
	public static Location middle(Location l1, Location l2) {
		return middle(l1, l2, l1.getWorld());
	}

	/**
	 * Creates a location in the middle of two points.
	 *
	 * @param c1    the first point
	 * @param c2    the second point
	 * @param world the location's world
	 */
	public static Location middle(Coordinates c1, Coordinates c2, World world) {
		double x = (c1.getX() + c2.getX()) / 2.0;
		double y = (c1.getY() + c2.getY()) / 2.0;
		double z = (c1.getZ() + c2.getZ()) / 2.0;
		return new Location(x, y, z, world);
	}
}