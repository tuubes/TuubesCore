package org.tuubes.core.items

import org.tuubes.core.engine.{Actor, AttributeStorage, ExecutionGroup}

/**
 * A stack of items.
 */
trait ItemStack extends Actor {
  /** @return the item's type */
  def typ: ItemType[_]

  /** @return the number of items in the stack */
  def quantity: Int

  /**
   * Manipulates the item's attributes in a thread-safe manner.
   * If the item has no attribute the function may never be called.
   *
   * @param f            the function to execute
   * @param currentGroup the current ExecutionGroup
   */
  def safely(f: AttributeStorage => ())(implicit currentGroup: ExecutionGroup): Unit
}
