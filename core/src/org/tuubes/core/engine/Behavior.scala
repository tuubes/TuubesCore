package org.tuubes.core.engine

/**
 * A behavior composes a GameObject and makes it react to messages.
 * A behavior can safely access the GameObject's attributes, and may have an internal state.
 *
 * @author TheElectronWill
 */
trait Behavior {
  /**
   * Reacts to a message received by the GameObject.
   *
   * @param msg  the message
   * @param attr the GameObject's attributes
   */
  def onMessage(msg: ActorMessage, attr: AttributeStorage): Unit

  /**
   * Reacts to a GameObeject update.
   *
   * @param dt   the time elapsed since the last update
   * @param attr the GameObjetct's attributes
   */
  def update(dt: Double, attr: AttributeStorage): Unit
}
