package org.tuubes.core.engine

import org.tuubes.core.worlds.LocalWorld

import scala.collection.mutable.ArrayBuffer

/**
 * A GameObject is an actor made of attributes and behaviors.
 * ==Attributes==
 * Attributes are pieces of data accessible by all the behaviors. The attributes' changes
 * are tracked to ease their synchronization with the game clients.
 * Attributes aren't accessible outside of the GameObject in order to ensure thread-safety.
 *
 * ==Behaviors==
 * Behaviors are pieces of code that make the GameObject react to incoming messages and
 * updates. They aren't accessible outside of the GameObject.
 *
 * @author TheElectronWill
 */
class GameObject extends GroupedActor {
  private[this] val attributes = new AttributeStorage()
  private[this] val behaviors = new ArrayBuffer[Behavior]
  private[core] var world: LocalWorld = _
  private[core] var id: Int = -1

  override protected final def onMessage(msg: ActorMessage): Unit = {
    super.onMessage(msg)
    for (behavior <- behaviors) {
      behavior.onMessage(msg, attributes)
    }
  }

  override private[core] final def update(dt: Double): Unit = {
    // Updates each behavior
    for (behavior <- behaviors) {
      behavior.update(dt, attributes)
    }
    // Notifies each property change
    for (property <- attributes) { // Uses the efficient foreach (see PropertyStorage#foreach)
      if (property.hasChanged) {
        property.endCycle()
      }
    }
  }
}
