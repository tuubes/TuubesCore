package org.tuubes.item

/** Base trait for items. */
trait Item {
  def maxStackSize: Int
}

/** Item companion object and registry. */
object Item extends TypeRegistry[Item, ItemType] {
  override protected def newType[I <: Item](provider: ()=>I, tag: ClassTag[I], id: Int) = ItemType(provider, tag, id)
}

/** Represents the game type of an item. */
final class ItemType[I <: Item](build: ()=>I, tag: ClassTag[I], id: Int) extends RegisteredType[I](build, tag, id)

trait StatefulItem extends Item with Updatable {
  def inventory: Inventory
  def stackSize: Int
}

trait Inventory {
  def capacity: Int
  def stackCount: Int
  def foreach(f: (Item, Int)=>Unit): Unit
}
