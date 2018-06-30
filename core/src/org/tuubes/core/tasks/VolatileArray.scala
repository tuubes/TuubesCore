package org.tuubes.core.tasks

/**
 * @author TheElectronWill
 */
final class VolatileArray[A](size: Int) extends Iterable[A] {
  private[this] var array = new Array[Any](size)

  def update(idx: Int, element: A): Unit = {
    val arr = array // volatile read
    arr(idx) = element
    array = arr // volatile write
    // visibility of the new element is ensured by the volatile read/write
  }

  def apply(idx: Int): A = array(idx).asInstanceOf[A]

  def underlying: Array[Any] = array

  override def iterator = new Iterator[A] {
    private[this] var idx = 0

    def next(): A = {
      val v = array(idx).asInstanceOf[A]
      idx += 1
      v
    }

    def hasNext: Boolean = idx < array.length
  }
}
