package org.tuubes.core.engine

/**
 * Asynchronous source of events consumed by observers (or "listeners").
 *
 * @tparam A events emitted by this observables
 */
trait Observable[A] {
  def subscribe(f: A => Unit): Unit
  // TODO add unsubscribe
  // TODO add functional operations like filter and map
}
trait BiObservable[A, B] {
  def subscribe(f: (A, B) => Unit): Unit
  // TODO add unsubscribe
  // TODO add functional operations like filter and map
}
