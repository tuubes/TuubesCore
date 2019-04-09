package org.tuubes.core.entities

import com.electronwill.util.Location
import org.tuubes.core.engine.{Actor, AttributeStorage, ExecutionGroup}

/**
 * An entity is an active element of the game that is neither a [[org.tuubes.core.blocks.Block]]
 * nor an [[org.tuubes.core.items.ItemStack]]. Players, animals and monsters are entities.
 */
trait Entity[S <: EntityState[S]] extends Actor {
  /** @return the entity's type */
  def typ: EntityType[S]

  /** @return the entity's location */
  def location: Location

  /**
   * Checks if the entity exists.
   *
   * @return true if it exists, false if it has despawned
   */
  def exists: Boolean

  /**
   * Removes this entity from the game world.
   */
  def despawn(): Unit

  /**
   * Captures the state of this entity.
   *
   * @return the coherent captured state
   */
  def captureState(): S

  /**
   * Manipulates the entity's attributes in a thread-safe manner.
   * If the entity has no attribute the function may never be called.
   *
   * @param f            the function to execute
   * @param currentGroup the current ExecutionGroup
   */
  def safely(f: AttributeStorage => Unit)(implicit currentGroup: ExecutionGroup): Unit
}
