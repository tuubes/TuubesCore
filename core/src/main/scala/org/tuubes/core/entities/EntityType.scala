package org.tuubes.core.entities

import org.tuubes.core.{Type, TypeRegistry}

/**
 * A type of entity.
 *
 * @author TheElectronWill
 */
abstract class EntityType(n: String) extends Type[EntityType](n, EntityType) {}

/**
 * Companion object and registry of entity types.
 */
object EntityType extends TypeRegistry[EntityType] {}