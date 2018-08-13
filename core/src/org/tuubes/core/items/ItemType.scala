package org.tuubes.core.items

import org.tuubes.core.{Type, TypeRegistry}

/**
 * A type of item.
 *
 * @author TheElectronWill
 */
abstract class ItemType[S <: ItemState](n: String) extends Type[ItemType[_]](n, ItemType) {
  //TODO
}

/**
 * Companion object and registry of item types.
 */
object ItemType extends TypeRegistry[ItemType[_]] {}
