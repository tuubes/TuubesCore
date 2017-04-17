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

import java.util.Objects;
import org.mcphoton.world.World;

/**
 * A location is a precise point defined by 3 coordinates (x,y,z) and one World. This class is mutable and
 * isn't thread-safe.
 *
 * @author TheElectronWill
 */
public final class MutableLocation implements Location {

	private double x, y, z;
	private World w;

	public MutableLocation(double x, double y, double z, World w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	/**
	 * Creates a new location that is the result of adding the vector v to this location.
	 */
	public void add(DoubleVector v) {
		x += v.x;
		y += v.y;
		z += v.z;
	}

	/**
	 * Creates a new location that is the result of adding the vector v to this location.
	 */
	public MutableLocation add(IntVector v) {
		return new MutableLocation(x + v.x, y + v.y, z + v.z, w);
	}

	/**
	 * Creates a new location that is the result of adding the location l to this location.
	 */
	public MutableLocation add(MutableLocation l) {
		return new MutableLocation(x + l.x, y + l.y, z + l.z, w);
	}

	/**
	 * Creates a new location that is the result of adding the specified numbers to this location.
	 */
	public MutableLocation add(double dx, double dy, double dz) {
		return new MutableLocation(x + dx, y + dy, z + dz, w);
	}

	@Override
	public MutableLocation clone() {
		return new MutableLocation(x, y, z, w);
	}

	@Override
	public World getWorld() {
		return w;
	}

	public void setWorld(World w) {
		this.w = w;
	}

	@Override
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	@Override
	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	@Override
	public int getBlockX() {
		return (int) x;
	}

	@Override
	public int getBlockY() {
		return (int) y;
	}

	@Override
	public int getBlockZ() {
		return (int) z;
	}

	@Override
	public MutableLocation withX(double x) {
		return new MutableLocation(x, y, z, w);
	}

	@Override
	public MutableLocation withY(double y) {
		return new MutableLocation(x, y, z, w);
	}

	@Override
	public MutableLocation withZ(double z) {
		return new MutableLocation(x, y, z, w);
	}

	@Override
	public MutableLocation withWorld(World w) {
		return new MutableLocation(x, y, z, w);
	}

	/**
	 * Creates a new ImmutableLocation with the coordinates and world of this MutableLocation.
	 */
	public MutableLocation toImmutableLocation() {
		return new MutableLocation(x, y, z, w);
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 89 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
		hash = 89 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
		hash = 89 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
		hash = 89 * hash + Objects.hashCode(this.w);
		return hash;
	}

	@Override
	public String toString() {
		return "MutableLocation{" + "x=" + x + ", y=" + y + ", z=" + z + ", world=" + w + '}';
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof MutableLocation) {
			MutableLocation l = (MutableLocation) obj;
			return w == l.w && x == l.x && y == l.y && z == l.z;
		}
		return false;
	}

	/**
	 * Creates a location in the middle of the two specified locations. The world will be the one of
	 * <code>l1</code>.
	 */
	public static MutableLocation middle(MutableLocation l1, MutableLocation l2) {
		double x = (l1.getX() + l2.getX()) / 2d;
		double y = (l1.getY() + l2.getY()) / 2d;
		double z = (l1.getZ() + l2.getZ()) / 2d;
		return new MutableLocation(x, y, z, l1.getWorld());
	}
}
