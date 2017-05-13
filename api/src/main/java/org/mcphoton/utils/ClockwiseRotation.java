package org.mcphoton.utils;

/**
 * A clockwise rotation, in steps of 45 degrees.
 *
 * @author TheElectronWill
 */
public enum ClockwiseRotation {

	CLOCKWISE_0,
	CLOCKWISE_45,
	CLOCKWISE_90,
	CLOCKWISE_135,
	CLOCKWISE_180,
	CLOCKWISE_225,
	CLOCKWISE_270,
	CLOCKWISE_315;

	/**
	 * Gets the ClockwiseRotation that corresponds to the specified number of clockwise 45-degrees
	 * rotations, starting from 0.
	 *
	 * @param t45 the number of 45-degrees rotations to do.
	 */
	public static ClockwiseRotation get(int t45) {
		return ClockwiseRotation.values()[t45 & 7];
	}

	/**
	 * Gets the rotation's id (between 0 and 7).
	 */
	public int getId() {
		return ordinal();
	}

	/**
	 * Turn clockwise starting from this rotation.
	 *
	 * @param t45 the number of 45-degrees rotation steps to do.
	 */
	public ClockwiseRotation turnClockwise(int t45) {
		return ClockwiseRotation.values()[(ordinal() + t45) & 7];
	}

	/**
	 * Turn counter-clockwise starting from this rotation.
	 *
	 * @param t45 the number of 45-degrees rotation steps to do.
	 */
	public ClockwiseRotation turnCounterClockwise(int t45) {
		return ClockwiseRotation.values()[(ordinal() - t45) & 7];
	}
}