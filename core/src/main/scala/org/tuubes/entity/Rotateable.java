package org.tuubes.entity;

/**
 * Something that can rotate.
 *
 * @author TheElectronWill
 * @see org.tuubes.utils.EulerAngles
 */
public interface Rotateable {
	/**
	 * @return the pitch in radians.
	 */
	float getPitch();

	/**
	 * Sets the pitch in radians.
	 *
	 * @param pitch the pitch to set.
	 */
	void setPitch(float pitch);

	/**
	 * @return the yaw in radians.
	 */
	float getYaw();

	/**
	 * Sets the yaw in radians.
	 *
	 * @param yaw the yaw to set.
	 */
	void setYaw(float yaw);
}