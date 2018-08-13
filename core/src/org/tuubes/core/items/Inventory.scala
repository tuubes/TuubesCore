package org.tuubes.core.items

/**
 * An inventory which contains item stacks.
 */
trait Inventory extends Iterable[ItemStack] {
  /**
   * Adds an item stack to the inventory.
   *
   * @param t the item type to use
   */
  def +=(t: ItemType[_]): Unit

  /**
   * Adds an item stack to the inventory.
   *
   * @param s the item state to apply
   */
  def +=(s: ItemState): Unit

  /**
   * Removes the first item stack that has the given type.
   *
   * @param t the type to remove
   */
  def -=(t: ItemType[_]): Unit = removeFirst(t)

  /**
   * Removes the first item stack that has the given type.
   *
   * @param t the type to remove
   */
  def removeFirst(t: ItemType[_]): Unit

  /**
   * Removes all the elements from the inventory.
   */
  def clear(): Unit

  /**
   * Gets an item stack from the inventory.
   *
   * @return the type of the stack at the given index
   */
  def getType(i: Int): ItemType[_]

  /**
   * Puts a new stack of the given type in the inventory.
   *
   * @param i the stack index
   * @param t the type to set
   */
  def setType(i: Int, t: ItemType[_]): ItemStack

  /**
   * Gets the ItemStack at the given index
   *
   * @return the stack at the given index
   */
  def getRef(i: Int): ItemStack

  /**
   * Captures the state of an ItemStack.
   *
   * @param i the stack index
   * @return the captured, coherent state of the stack
   */
  def captureState(i: Int): ItemState

  /**
   * Puts a new ItemStack created from the given ItemState in the inventory.
   *
   * @param i the index of the stack to replace
   * @param s the state to use
   */
  def applyState(i: Int, s: ItemState): Unit

  /**
   * Iterates on the ItemTypes. Avoids to create ItemStack instances for simple items.
   */
  def types: Iterable[ItemType[_]]

  /**
   * Iterates on the quantities. Avoids to create ItemStack instances for simple items.
   */
  def quantities: Iterable[Int]
}
