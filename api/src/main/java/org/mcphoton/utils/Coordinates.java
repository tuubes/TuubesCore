package org.mcphoton.utils;

/**
 * Represents 3D coordinates (x,y,z).
 *
 * @author TheElectronWill
 */
public interface Coordinates {
	/**
	 * @return the x coordinate
	 */
	double getX();

	/**
	 * @return the y coordinate
	 */
	double getY();

	/**
	 * @return the z coordinate
	 */
	double getZ();

	/**
	 * Calculates the squared distance between two points.
	 *
	 * @return the squared distance between the points.
	 */
	static double squaredDist(Coordinates a, Coordinates b) {
		double deltaX = b.getX() - a.getX();
		double deltaY = b.getY() - a.getY();
		double deltaZ = b.getZ() - a.getZ();
		return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
	}

	/**
	 * Calculates the distance between these coordinates and another one. If you want  the squared
	 * distance use {@link #squaredDist(Coordinates, Coordinates)}.
	 *
	 * @return the distance between this location and l.
	 */
	static double dist(Coordinates a, Coordinates b) {
		return Math.sqrt(squaredDist(a, b));
	}
}