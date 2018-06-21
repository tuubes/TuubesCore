package org.tuubes.core.engine

import com.electronwill.collections.RecyclingIndex
import org.tuubes.core.worlds.LocalWorld

import scala.collection.mutable.ArrayBuffer

/**
 * @author TheElectronWill
 */
final class GameObject extends LocalActor {
  private[this] val props = new PropertyStorage()
  private[this] val behaviors = new ArrayBuffer[Behavior]
  private[this] val updateListeners = new RecyclingIndex[Runnable]
  private[this] var _world: LocalWorld = _
  private[this] var _id: Int = _

  def properties: PropertyStorage = props
  def world: LocalWorld = _world
  def id: Int = _id

  private[tuubes] def world_=(w: LocalWorld) = _world = w
  private[tuubes] def id_=(id: Int) = _id = id

  override protected def onMessage(msg: ActorMessage): Unit = {
    super.onMessage(msg)
    for (behavior <- behaviors) {
      behavior.onMessage(msg, this)
    }
  }
  override protected def filter(msg: ActorMessage): Boolean = true

  override def update(dt: Double): Unit = {
    // Updates each behavior
    for (behavior <- behaviors) {
      behavior.update(dt, this)
    }
    // Notifies each property change
    for (property <- props) { // Uses the efficient foreach (see PropertyStorage#foreach)
      if (property.hasChanged) {
        property.endCycle()
      }
    }
    // Notifies each update listener
    for (listener <- updateListeners.valuesIterator) {
      listener.run()
    }
  }

  /**
	 * Adds a listener to get notified after each update of this GameObject.
	 *
	 * @param listener the listener
	 * @return a ListenKey to use with [[unlistenUpdate]]
	 */
  def listenUpdate(listener: Runnable): ListenKey[GameObject] = {
    new ListenKey(updateListeners += listener)
  }

  /**
	 * Adds a listener to get notified after each update of this GameObject.
	 *
	 * @param listener the listener
	 * @return a ListenRegistration to remove the listener
	 */
  def rlistenUpdate(listener: Runnable): ListenRegistration[GameObject] = {
    new ListenRegistration(listenUpdate(listener), unlistenUpdate)
  }

  /**
	 * Removes a listener from this GameObject.
	 *
	 * @param key the listener's key
	 */
  def unlistenUpdate(key: ListenKey[GameObject]): Unit = {
    updateListeners.remove(key.id)
  }
}
