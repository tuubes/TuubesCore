package org.tuubes.core.engine

/**
 * Asynchronous source of events consumed by observers (or "listeners").
 */
trait Observable[T, +A] {
  def subscribe(observer: Observer[A]): Registration[T]
  // TODO add unsubscribe
  // TODO add functional operations like filter and map
}
trait BiObservable[T, +A, +B] {
  def subscribe(observer: BiObserver[A, B]): Registration[T]
  // TODO add unsubscribe
  // TODO add functional operations like filter and map
}
trait TriObservable[T, +A, +B, +C] {
  def subscribe(observer: TriObserver[A, B, C]): Registration[T]
  // TODO add unsubscribe
  // TODO add functional operations like filter and map
}
