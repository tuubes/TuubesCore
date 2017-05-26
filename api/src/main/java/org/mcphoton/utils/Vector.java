package org.mcphoton.utils;

import org.mcphoton.world.World;

/**
 * A mutable 3D vector.
 *
 * @author TheElectronWill
 */
public final class Vector implements Coordinates, Cloneable {
	private double x, y, z;

	/**
	 * Creates a new vector (0,0,0).
	 */
	public Vector() {
		this(0, 0, 0);
	}

	/**
	 * Creates a new vector with the specified coordinates.
	 */
	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector(Coordinates coords) {
		this(coords.getX(), coords.getY(), coords.getZ());
	}

	@Override
	public Vector clone() {
		return new Vector(x, y, z);
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

	public Vector setX(double x) {
		this.x = x;
		return this;
	}

	public Vector setY(double y) {
		this.y = y;
		return this;
	}

	public Vector setZ(double z) {
		this.z = z;
		return this;
	}

	public Vector addX(double delta) {
		this.x += delta;
		return this;
	}

	public Vector addY(double delta) {
		this.y += delta;
		return this;
	}

	public Vector addZ(double delta) {
		this.z += delta;
		return this;
	}

	public Vector add(Vector v) {
		x += v.x;
		y += v.y;
		z += v.z;
		return this;
	}

	public Vector add(Coordinates c) {
		x += c.getX();
		y += c.getY();
		z += c.getZ();
		return this;
	}

	public Vector sub(Vector v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
		return this;
	}

	public Vector sub(Coordinates v) {
		x -= v.getX();
		y -= v.getY();
		z -= v.getZ();
		return this;
	}

	public Vector add(double k) {
		x += k;
		y += k;
		z += k;
		return this;
	}

	public Vector multiply(double k) {
		x *= k;
		z *= k;
		y *= k;
		return this;
	}

	public Vector divide(double k) {
		x /= k;
		z /= k;
		y /= k;
		return this;
	}

	/**
	 * Negates this vector: multiply it by -1.
	 *
	 * @return this vector
	 */
	public Vector negate() {
		return multiply(-1);
	}

	/**
	 * Normalizes this vector: divides its coordinates by its norm so that its length becames 1.
	 *
	 * @return this vector
	 */
	public Vector normalize() {
		return divide(norm());
	}

	/**
	 * Calculates the squared distance between the point corresponding to this vector and the point
	 * corresponding to the vector v. This is faster than manually multiplying the distance by
	 * itself.
	 */
	public double squaredDist(Vector v) {
		double deltaX = v.x - x, deltaY = v.y - y, deltaZ = v.z - z;
		return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
	}

	/**
	 * Calculates the squared distance between the point corresponding to this vector and the point
	 * corresponding to the vector v.
	 */
	public double dist(Vector v) {
		return Math.sqrt(squaredDist(v));
	}

	/**
	 * Calculates the squared norm of this vector. This is faster than manually multiplying the
	 * norm by itself.
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
	 * Calculates the angle (in radians) between this vector and the vector v.
	 */
	public double angle(Vector v) {
		return Math.acos(this.dot(v) / (this.norm() * v.norm()));
	}

	/**
	 * Calculates the dot product (or "scalar product") of this vector with the vector v.
	 */
	public double dot(Vector v) {
		return x * v.x + y * v.y + z * v.z;
	}

	/**
	 * Calculates the cross product (or "vector product") of this vector with the vector v.
	 */
	public Vector cross(Vector v) {
		double px = y * v.z - z * v.y;
		double py = z * v.x - x * v.z;
		double pz = x * v.y - y * v.x;
		return new Vector(px, py, pz);
	}

	/**
	 * Creates a new ImmutableLocation with the coordinates of this vector.
	 *
	 * @param w the location's world.
	 */
	public Location toLocation(World w) {
		return new Location(x, y, z, w);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + Double.hashCode(x);
		hash = 31 * hash + Double.hashCode(y);
		hash = 31 * hash + Double.hashCode(z);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Vector) {
			Vector v = (Vector)obj;
			return v.x == x && v.y == y && v.z == z;
		}
		return false;
	}

	/**
	 * Checks if this vector is the null vector (0,0,0).
	 */
	public boolean isNull() {
		return x == 0 && y == 0 && z == 0;
	}

	/**
	 * Checks if this vector is the null vector, with some tolerance.
	 * <p>
	 * For instance, if the tolerance is 0.01:
	 * <li>(0,0,0) is nearly null</li>
	 * <li>(0.009,-0.009,0.00999999) is nearly null</li>
	 * <li>(0.01,0,0) isn't nearly null</li>
	 */
	public boolean isNearlyNull(double tolerance) {
		return Math.abs(x) < tolerance && Math.abs(y) < tolerance && Math.abs(z) < tolerance;
	}

	/**
	 * Checks if this vector is nearly equals to the vector v, with some tolerance.
	 */
	public boolean isNearlyEqualTo(Vector v, double tolerance) {
		return Math.abs(v.x - x) < tolerance
			   && Math.abs(v.y - y) < tolerance
			   && Math.abs(v.z - z) < tolerance;
	}

	/**
	 * Checks if this vector is orthogonal the vector v, with some tolerance.
	 */
	public boolean isNearlyOrthogonalTo(Vector v, double tolerance) {
		return Math.abs(this.dot(v) / (this.norm() * v.norm())) < tolerance;
	}

	/**
	 * Checks if this vector is collinear to the vector v, with some tolerance.
	 */
	public boolean isNearlyCollinearTo(Vector v, double tolerance) {
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
	 * Checks if all the coordinates of this vector are bigger than the ones of the vector v.
	 */
	public boolean isBiggerThan(Vector v) {
		return x > v.x && y > v.y && z > v.z;
	}

	/**
	 * Checks if all the coordinates of this vector are smaller than the ones of the vector v.
	 */
	public boolean isSmallerThan(Vector v) {
		return x < v.x && y < v.y && z < v.z;
	}

	/**
	 * Gets the middle of two vectors.
	 */
	public static Vector middle(Vector v1, Vector v2) {
		double x = (v1.x + v2.x) / 2d, y = (v1.y + v2.y) / 2d, z = (v1.z + v2.z) / 2d;
		return new Vector(x, y, z);
	}

	/**
	 * Sums some vectors.
	 */
	public static Vector sum(Vector... vectors) {
		Vector result = new Vector();
		for (Vector v : vectors) {
			result.add(v);
		}
		return result;
	}

	/**
	 * Gets a vector such that each coordinate is the maximum of the coordinates of the specified
	 * vectors.
	 */
	public static Vector max(Vector... vectors) {
		Vector first = vectors[0];
		double maxX = first.x, maxY = first.y, maxZ = first.z;
		for (int i = 1; i < vectors.length; i++) {
			Vector v = vectors[i];
			if (v.x > maxX) { maxX = v.x; }
			if (v.y > maxY) { maxY = v.y; }
			if (v.z > maxZ) { maxZ = v.z; }
		}
		return new Vector(maxX, maxY, maxZ);
	}

	/**
	 * Gets a vector such that each coordinate is the minimum of the coordinates of the specified
	 * vectors.
	 */
	public static Vector min(Vector... vectors) {
		Vector first = vectors[0];
		double minX = first.x, minY = first.y, minZ = first.z;
		for (int i = 1; i < vectors.length; i++) {
			Vector v = vectors[i];
			if (v.x < minX) { minX = v.x; }
			if (v.y < minY) { minY = v.y; }
			if (v.z < minZ) { minZ = v.z; }
		}
		return new Vector(minX, minY, minZ);
	}
}