package org.mcphoton.utils;

/**
 * An EulerAngle is composed of 3 rotations (yaw, pitch, roll) on 3 axis. All angles are in radians.
 * This class isn't thread-safe.
 *
 * @author DJmaxZPLAY
 * @author TheElectronWill
 * @see <a href="https://en.wikipedia.org/wiki/Euler_angles">Wikipedia - Euler Angles</a>
 */
public class EulerAngles {
	private float yaw, pitch, roll;

	/**
	 * Creates an EulerAngle(0, 0, 0)
	 */
	public EulerAngles() {
		this(0, 0, 0);
	}

	/**
	 * Creates an EulerAngle with 3 rotations angles (in radians).
	 */
	public EulerAngles(float yaw, float pitch, float roll) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.roll = roll;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getRoll() {
		return roll;
	}

	public void setRoll(float roll) {
		this.roll = roll;
	}

	public EulerAngles add(float yaw, float pitch, float roll) {
		this.yaw += yaw;
		this.pitch += pitch;
		this.roll += roll;
		return this;
	}

	public EulerAngles add(EulerAngles a) {
		return add(a.yaw, a.pitch, a.roll);
	}

	public EulerAngles subtract(float yaw, float pitch, float roll) {
		this.yaw -= yaw;
		this.pitch -= pitch;
		this.roll -= roll;
		return this;
	}

	public EulerAngles substract(EulerAngles a) {
		return add(a.yaw, a.pitch, a.roll);
	}

	@Override
	public String toString() {
		return "(yaw:" + yaw + ", pitch:" + pitch + ", roll:" + roll + ')';
	}
}