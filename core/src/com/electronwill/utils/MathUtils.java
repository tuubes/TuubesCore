package com.electronwill.utils;

/**
 * @author TheElectronWill
 */
public final class MathUtils {
  private MathUtils() {}

  public static final float TWICE_PI = (float)(Math.PI * 2.0);
  public static final float HALF_PI = (float)(Math.PI * 0.5);
  public static final float NEG_HALF_PI = (float)(Math.PI * -0.5);
  public static final float INV_TWICE_PI = 1f / TWICE_PI;
  public static final float INV_HALF_PI = 1f / HALF_PI;

  /**
   * Forces a value to be in the given range.
   *
   * @param value the value
   * @param min   the minimum value allowed
   * @param max   the maximum value allowed
   * @return the new value, in the given range
   */
  public static int forceRange(int value, int min, int max) {
    if (value < min) return min;
    else if (value > max) return max;
    else return value;
  }

  /**
   * Forces a value to be in the given range.
   *
   * @param value the value
   * @param min   the minimum value allowed
   * @param max   the maximum value allowed
   * @return the new value, in the given range
   */
  public static float forceRange(float value, float min, float max) {
    if (value < min) return min;
    else if (value > max) return max;
    else return value;
  }

  /**
   * Forces a value to be in the given range.
   *
   * @param value the value
   * @param min   the minimum value allowed
   * @param max   the maximum value allowed
   * @return the new value, in the given range
   */
  public static double forceRange(double value, double min, double max) {
    if (value < min) return min;
    else if (value > max) return max;
    else return value;
  }

  /**
   * Normalizes a value by adding or removing {@code step} until the value is in the given range.
   * The returned value isn't guaranteed to be in the given range, it depends on the step.
   *
   * @param value the value
   * @param min   the minimum value allowed
   * @param max   the maximum value allowed
   * @param step  the step to use
   * @return the new value, in the given range (best effort)
   */
  public static int stepNormalize(int value, int min, int max, int step) {
    while (value < min) value += step;
    while (value > max) value -= step;
    return value;
  }

  /**
   * Normalizes a value by adding or removing {@code step} until the value is in the given range.
   * The returned value isn't guaranteed to be in the given range, it depends on the step.
   *
   * @param value the value
   * @param min   the minimum value allowed
   * @param max   the maximum value allowed
   * @param step  the step to use
   * @return the new value, in the given range (best effort)
   */
  public static float stepNormalize(float value, float min, float max, float step) {
    while (value < min) value += step;
    while (value > max) value -= step;
    return value;
  }

  /**
   * Normalizes a value by adding or removing {@code step} until the value is in the given range.
   * The returned value isn't guaranteed to be in the given range, it depends on the step.
   *
   * @param value the value
   * @param min   the minimum value allowed
   * @param max   the maximum value allowed
   * @param step  the step to use
   * @return the new value, in the given range (best effort)
   */
  public static double stepNormalize(double value, double min, double max, double step) {
    while (value < min) value += step;
    while (value > max) value -= step;
    return value;
  }

  /**
   * Checks if two floats are close enough.
   *
   * @param a         the first value
   * @param b         the second value
   * @param tolerance the comparison's tolerance
   * @return true if {@code abs(a-b) <= tolerance}, false otherwise
   */
  public static boolean almostEqual(float a, float b, float tolerance) {
    return (a > b) ? (a - b <= tolerance) : (b - a <= tolerance);
  }

  /**
   * Checks if two doubles are close enough.
   *
   * @param a         the first value
   * @param b         the second value
   * @param tolerance the comparison's tolerance
   * @return true if {@code abs(a-b) <= tolerance}, false otherwise
   */
  public static boolean almostEqual(double a, double b, double tolerance) {
    return (a > b) ? (a - b <= tolerance) : (b - a <= tolerance);
  }
}
