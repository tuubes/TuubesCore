package org.tuubes.core.entities

import com.electronwill.util.Location
import org.tuubes.core.{Type, TypeRegistry}

/**
 * A type of entity.
 *
 * @author TheElectronWill
 */
abstract class EntityType[S <: EntityState[S]](n: String) extends Type[EntityType[_]](n, EntityType) {
  /**
   * Creates a new [[Entity]] instance for the entity that is being created at the given
   * location. Returns None if no instance of [[Entity]] is needed, which happens when the entity
   * carries no information other than its type and doesn't update.
   *
   * @param loc the entity's location
   * @return the entity instance, or None if no Entity instance is needed
   */
  protected[core] def newEntity(loc: Location): Entity[S]

  /**
   * Creates a new [[Entity]] instance for the entity that is being created at the given
   * location, and applies the given [[EntityState]]. Returns None if no instance of [[Entity]] is
   * needed, which happens when the entity carries no information other than its type and doesn't
   * update.
   *
   * @param loc the entity's location
   * @return the entity instance, or None if no Entity instance is needed
   */
  protected[core] def newEntity(loc: Location, state: S): Entity[S]

  /**
   * Creates a [[EntityState]] with some default values for this EntityType.
   *
   * @return a new EntityState with default values
   */
  def newState(): S
}

/**
 * Companion object and registry of entity types.
 */
object EntityType extends TypeRegistry[EntityType[_]] {}
