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
 * A spatial vector defined by 3 floating-point (doubles) coordinates. It isn't thread-safe.
 *
 * @author TheElectronWill
 */
public final class DoubleVector implements Cloneable {

	double x, y, z;

	/**
	 * Creates a new vector (0,0,0).
	 */
	public DoubleVector() {
		this(0, 0, 0);
	}

	/**
	 * Creates a new vector with the specified coordinates.
	 */
	public DoubleVector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public DoubleVector clone() {
		return new DoubleVector(x, y, z);
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

	public DoubleVector setX(double x) {
		this.x = x;
		return this;
	}

	public DoubleVector setY(double y) {
		this.y = y;
		return this;
	}

	public DoubleVector setZ(double z) {
		this.z = z;
		return this;
	}

	public DoubleVector addX(double delta) {
		this.x += delta;
		return this;
	}

	public DoubleVector addY(double delta) {
		this.y += delta;
		return this;
	}

	public DoubleVector addZ(double delta) {
		this.z += delta;
		return this;
	}

	public DoubleVector add(DoubleVector v) {
		x += v.x;
		y += v.y;
		z += v.z;
		return this;
	}

	public DoubleVector substract(DoubleVector v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
		return this;
	}

	public DoubleVector add(double k) {
		x += k;
		y += k;
		z += k;
		return this;
	}

	public DoubleVector multiply(double k) {
		x *= k;
		z *= k;
		y *= k;
		return this;
	}

	public DoubleVector divide(double k) {
		x /= k;
		z /= k;
		y /= k;
		return this;
	}

	/**
	 * Normalizes this vector: divides its coordinates by its norm so that its length is 1.
	 */
	public DoubleVector normalize() {
		return divide(norm());
	}

	/**
	 * Calculates the squared distance between the point corresponding to this vector and the point
	 * corresponding to the vector v. This is faster than manually multiplying the distance by itself.
	 */
	public double squaredDistance(DoubleVector v) {
		double deltaX = v.x - x, deltaY = v.y - y, deltaZ = v.z - z;
		return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
	}

	/**
	 * Calculates the squared distance between the point corresponding to this vector and the point
	 * corresponding to the vector v.
	 */
	public double distance(DoubleVector v) {
		double deltaX = v.x - x, deltaY = v.y - y, deltaZ = v.z - z;
		return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
	}

	/**
	 * Calculates the squared norm of this vector. This is faster than manually
	 * multiplying the norm by itself.
	 */
	public double squaredNorm() {
		return x * x + y * y + z * z;
	}

	/**
	 * Calculates the norm (length) of this vector.
	 */
	public double norm() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	/**
	 * Calculates the angle (in radians)between this vector and the vector v.
	 */
	public double angleBetween(DoubleVector v) {
		return Math.acos(this.dotProduct(v) / (this.norm() * v.norm()));
	}

	/**
	 * Calculates the dot product (or "scalar product") of this vector with the vector v.
	 */
	public double dotProduct(DoubleVector v) {
		return x * v.x + y * v.y + z * v.z;
	}

	/**
	 * Calculates the cross product (or "vector product") of this vector with the vector v.
	 */
	public DoubleVector crossProduct(DoubleVector v) {
		double px = y * v.z - z * v.y;
		double py = z * v.x - x * v.z;
		double pz = x * v.y - y * v.x;
		return new DoubleVector(px, py, pz);
	}

	/**
	 * Checks if this vector is the null vector (0,0,0).
	 */
	public boolean isNull() {
		return x == 0 && y == 0 && z == 0;
	}

	/**
	 * Checks if this vector is orthogonal the vector v, with some tolerance.
	 */
	public boolean isNearlyOrthogonalTo(DoubleVector v, double tolerance) {
		return Math.abs(this.dotProduct(v) / (this.norm() * v.norm())) < tolerance;
	}

	/**
	 * Checks if this vector is collinear to the vector v, with some tolerance.
	 */
	public boolean isNearlyCollinearTo(DoubleVector v, double tolerance) {
		if (this.isNull() || v.isNull()) {
			return true;
		}
		if ((v.x == 0 && x != 0) || (v.y == 0 && y != 0) || (v.z == 0 && z == 0)) {
			return false;
		}
		double qx = x / v.x, qy = y / v.y, qz = z / v.z;
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
	 * Creates a new IntVector with the coordinates of this vector. Each coordinate is converted to an int.
	 */
	public IntVector toIntVector() {
		return new IntVector((int) x, (int) y, (int) z);
	}

	@Override
	public String toString() {
		return "DoubleVector(" + x + ", " + y + ", " + z + ')';
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 29 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
		hash = 29 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
		hash = 29 * hash + (int) (Double.doubleToLongBits(this.z) ^ (Double.doubleToLongBits(this.z) >>> 32));
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof DoubleVector) {
			DoubleVector v = (DoubleVector) obj;
			return v.x == x && v.y == y && v.z == z;
		} else if (obj instanceof IntVector) {
			IntVector v = (IntVector) obj;
			return v.x == x && v.y == y && v.z == z;
		}
		return false;
	}

	/**
	 * Checks if this vector is nearly equals to the vector v, with some tolerance.
	 */
	public boolean isNearlyEqualTo(DoubleVector v, double tolerance) {
		return Math.abs(v.x - x) < tolerance && Math.abs(v.y - y) < tolerance && Math.abs(v.z - z) < tolerance;
	}

	/**
	 * Checks if this vector is nearly equals to the vector v, with some tolerance.
	 */
	public boolean isNearlyEqualTo(IntVector v, double tolerance) {
		return Math.abs(v.x - x) < tolerance && Math.abs(v.y - y) < tolerance && Math.abs(v.z - z) < tolerance;
	}

	/**
	 * Creates a new DoubleVector that represents the middle of the two specified vectors.
	 */
	public static DoubleVector middle(DoubleVector v1, DoubleVector v2) {
		double x = (v1.x + v2.x) / 2d, y = (v1.y + v2.y) / 2d, z = (v1.z + v2.z) / 2d;
		return new DoubleVector(x, y, z);
	}

	/**
	 * Creates a new DoubleVector that represents the middle of the two specified vectors.
	 */
	public static DoubleVector middle(DoubleVector v1, IntVector v2) {
		double x = (v1.x + v2.x) / 2d, y = (v1.y + v2.y) / 2d, z = (v1.z + v2.z) / 2d;
		return new DoubleVector(x, y, z);
	}

	/**
	 * Creates a new DoubleVector that represents the middle of the two specified vectors.
	 */
	public static DoubleVector middle(IntVector v1, IntVector v2) {
		double x = (v1.x + v2.x) / 2d, y = (v1.y + v2.y) / 2d, z = (v1.z + v2.z) / 2d;
		return new DoubleVector(x, y, z);
	}

}
