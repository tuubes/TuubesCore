package org.tuubes.core.entities

/**
 * An EntityState describes the state of a entity. It doesn't track the current state of an existing
 * entity but rather stores a consistent snapshot of its state.
 */
trait EntityState[S] {
  /** @return the entity's type */
  def typ: EntityType[S]
}
