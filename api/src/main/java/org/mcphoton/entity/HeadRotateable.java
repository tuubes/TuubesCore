package org.mcphoton.entity;

/**
 * Something that can rotates its head side way.
 *
 * @author TheElectronWill
 */
public interface HeadRotateable extends Rotateable {
	/**
	 * @return the head yaw, in radians.
	 */
	float getHeadYaw();

	/**
	 * Sets the head yaw in radians.
	 *
	 * @param headYaw the yaw to set.
	 */
	void setHeadYaw(float headYaw);
}
