package org.tuubes.core.items

/**
 * An ItemState describes the state of an [[ItemStack]]. It doesn't track the current state of
 * an existing item stack but rather stores a consistent snapshot of its state.
 */
trait ItemState {
  /** @return the item's type */
  def typ: ItemType[_]
}
