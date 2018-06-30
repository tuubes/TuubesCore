package com.electronwill.utils

/**
 * Moving statistics.
 *
 * @param size the window's size, ie the number of last values kept
 */
final class MovingStats(size: Int) {
  private[this] val values = new Array[Double](size) // the last values
  private[this] var i = 0 // current index
  private[this] var n = 0 // number of values, useful at the beginning when the array isn't full

  private[this] var sum = 0.0 // sum(values)
  private[this] var sum2 = 0.0 // sum(values^2)
  private[this] var _mean = 0.0 // average
  private[this] var _var = 0.0 // variance (biased, not sample)

  /** Puts a new value */
  def put(newest: Double): Unit = {
    if (n < values.length) n += 1
    val oldest = values(i)
    values(i) = newest
    i = (i + 1) % values.length

    sum = sum - oldest + newest
    _mean = sum / n

    sum2 = sum2 - math.pow(oldest, 2) + math.pow(newest, 2)
    _var = sum2 / n - math.pow(_mean, 2)
  }

  /** @return the current mean */
  def mean: Double = _mean

  /** @return the current variance */
  def variance: Double = _var

  /** @return the current standard deviation */
  def stdev: Double = math.sqrt(_var)
}
