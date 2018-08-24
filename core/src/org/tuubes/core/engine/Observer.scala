package org.tuubes.core.engine

/**
 * Consumes events given by [[Observable]].
 *
 * @tparam A events emitted by the observable
 */
trait Observer[-A] {
  def onEvent(a: A): Unit
}
trait BiObserver[-A, -B] {
  def onEvent(a: A, b: B): Unit
}
trait TriObserver[-A, -B, -C] {
  def onEvent(a: A, b: B, c: C): Unit
}
