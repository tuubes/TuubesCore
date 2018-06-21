package com.electronwill.utils

final class MovingStats(size: Int) {
  private[this] val values = new Array[Double](size)
  private[this] var i = 0
  private[this] var n = 0

  private[this] var total = 0.0
  private[this] var total2 = 0.0
  private[this] var _mean = 0.0
  private[this] var _var = 0.0

  def put(newest: Double): Unit = {
    if(n < values.length) n+=1
    val oldest = values(i)
    values(i) = newest
    i = (i + 1) % values.length

    total = total - oldest + newest
    _mean = total/n

    total2 = total2 - math.pow(oldest, 2) + math.pow(newest, 2)
    _var = total2/n - math.pow(_mean, 2)
  }

  /** @return the current mean */
  def mean: Double = _mean

  /** @return the current variance */
  def variance: Double = _var

  /** @return the current standard deviation */
  def stdev: Double = math.sqrt(_var)
}
