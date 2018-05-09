package org.tuubes.core.items

import org.tuubes.core.{Type, TypeRegistry}

/**
 * A type of item.
 *
 * @author TheElectronWill
 */
abstract class ItemType(n: String) extends Type[ItemType](n, ItemType) {}

/**
 * Companion object and registry of item types.
 */
object ItemType extends TypeRegistry[ItemType] {}
