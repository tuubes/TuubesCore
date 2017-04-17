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
 * A spatial vector defined by 3 integers coordinates. It isn't thread-safe.
 *
 * @author TheElectronWill
 */
public final class IntVector implements Cloneable {

	int x, y, z;

	/**
	 * Creates a new vector (0,0,0).
	 */
	public IntVector() {
		this(0, 0, 0);
	}

	/**
	 * Creates a new vector with the specified coordinates.
	 */
	public IntVector(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public IntVector clone() {
		return new IntVector(x, y, z);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public IntVector setX(int x) {
		this.x = x;
		return this;
	}

	public IntVector setY(int y) {
		this.y = y;
		return this;
	}

	public IntVector setZ(int z) {
		this.z = z;
		return this;
	}

	public IntVector addX(int delta) {
		this.x += delta;
		return this;
	}

	public IntVector addY(int delta) {
		this.y += delta;
		return this;
	}

	public IntVector addZ(int delta) {
		this.z += delta;
		return this;
	}

	public IntVector add(IntVector v) {
		x += v.x;
		y += v.y;
		z += v.z;
		return this;
	}

	public IntVector substract(IntVector v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
		return this;
	}

	public IntVector add(int k) {
		x += k;
		y += k;
		z += k;
		return this;
	}

	public IntVector multiply(int k) {
		x *= k;
		z *= k;
		y *= k;
		return this;
	}

	public IntVector divide(int k) {
		x /= k;
		z /= k;
		y /= k;
		return this;
	}

	/**
	 * Normalizes this vector: divides its coordinates by its norm so that its length is 1. This method
	 * returns a <b>new DoubleVector</b> that represents the normalization of this IntVector. This IntVector
	 * is not modified.
	 */
	public DoubleVector normalize() {
		return new DoubleVector(x, y, z).divide(norm());
	}

	/**
	 * Calculates the squared distance between the point corresponding to this vector and the point
	 * corresponding to the vector v. This is faster than manually multiplying the distance by itself.
	 */
	public double squaredDistance(IntVector v) {
		double deltaX = v.x - x, deltaY = v.y - y, deltaZ = v.z - z;
		return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
	}

	/**
	 * Calculates the squared distance between the point corresponding to this vector and the point
	 * corresponding to the vector v.
	 */
	public double distance(IntVector v) {
		double deltaX = v.x - x, deltaY = v.y - y, deltaZ = v.z - z;
		return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
	}

	/**
	 * Calculates the squared norm of this vector. This is faster than manually
	 * multiplying the norm by itself.
	 */
	public int squaredNorm() {
		return x * x + y * y + z * z;
	}

	/**
	 * Calculates the norm (length) of this vector.
	 */
	public double norm() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	/**
	 * Calculates the angle (in radians) between this vector and the vector v.
	 */
	public double angleBetween(IntVector v) {
		return Math.acos(this.dotProduct(v) / (this.norm() * v.norm()));
	}

	/**
	 * Calculates the dot product (or "scalar product") of this vector with the vector v.
	 */
	public int dotProduct(IntVector v) {
		return x * v.x + y * v.y + z * v.z;
	}

	/**
	 * Calculates the cross product (or "vector product") of this vector with the vector v.
	 */
	public IntVector crossProduct(IntVector v) {
		int px = y * v.z - v.z * y;
		int py = z * v.x - x * v.z;
		int pz = x * v.y - y * v.x;
		return new IntVector(px, py, pz);
	}

	/**
	 * Checks if this vector is the null vector (0,0,0).
	 */
	public boolean isNull() {
		return x == 0 && y == 0 && z == 0;
	}

	/**
	 * Checks if this vector is orthogonal to the vector v.
	 */
	public boolean isOrthogonalTo(IntVector v) {
		return dotProduct(v) == 0;
	}

	/**
	 * Checks if this vector is collinear to the vector v.
	 */
	public boolean isNearlyCollinearTo(IntVector v, double tolerance) {
		if (this.isNull() || v.isNull()) {
			return true;
		}
		if ((v.x == 0 && x != 0) || (v.y == 0 && y != 0) || (v.z == 0 && z == 0)) {
			return false;
		}
		double qx = x / (double) v.x, qy = y / (double) v.y, qz = z / (double) v.z;
		return Math.abs(qx - qy) < tolerance && Math.abs(qy - qz) < tolerance;
	}

	/**
	 * Creates a new MutableLocation with the coordinates of this vector.
	 *
	 * @param w the location's world.
	 */
	public MutableLocation toMutableLocation(World w) {
		return new MutableLocation(x, y, z, w);
	}
	
	/**
	 * Creates a new ImmutableLocation with the coordinates of this vector.
	 *
	 * @param w the location's world.
	 */
	public ImmutableLocation toImmutableLocation(World w) {
		return new ImmutableLocation(x, y, z, w);
	}

	/**
	 * Creates a new DoubleVector with the coordinates of this vector. Each coordinate is converted to a
	 * double.
	 */
	public DoubleVector toDoubleVector() {
		return new DoubleVector(x, y, z);
	}

	@Override
	public String toString() {
		return "IntVector(" + x + ", " + y + ", " + z + ')';
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 37 * hash + this.x;
		hash = 37 * hash + this.y;
		hash = 37 * hash + this.z;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof IntVector) {
			IntVector v = (IntVector) obj;
			return v.x == x && v.y == y && v.z == z;
		} else if (obj instanceof DoubleVector) {
			DoubleVector v = (DoubleVector) obj;
			return v.x == x && v.y == y && v.z == z;
		}
		return false;
	}

	/**
	 * Checks if this vector is equal to the vector v.
	 */
	public boolean equals(IntVector v) {
		return x == v.x && y == v.y && z == v.z;
	}

}
