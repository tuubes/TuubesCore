package org.tuubes.core.entities

import com.electronwill.util.Vec3d

/**
 * Manages local entities.
 */
trait EntityManager {
  /**
   * Spawns an entity of some type.
   *
   * @param pos the spawn position
   * @param t   the entity's type
   * @return the newly created Entity object
   */
  def spawn[A](pos: Vec3d, t: EntityType[A]): Entity[A]

  /**
   * Spawns an entity of some type.
   *
   * @param pos the spawn position
   * @param s   the EntityState to apply to the entity
   * @return the newly created Entity object
   */
  def spawn[A](pos: Vec3d, s: EntityState[A]): Entity[A]
}
