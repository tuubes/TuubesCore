package org.tuubes.block

/** Base trait for blocks */
trait Block {
  def onRightClick(): Unit = {}
  def onLeftClick(): Unit = {}
}

/** Block companion object and registry.
  *
  * ## Obtaining a block
  * If you know the class of the block (at compile time), like `Stone`, use:
  * ```
  * val myStone = Block.create[Stone]
  * ```
  *
  * Otherwise, you need to have a `BlockType` and to do:
  * ```
  * val myBlock = Block.create(myType)
  * ```
  *
  * The difference is that, with `Block.create[B]` you know that you will exactly
  * get a block of type `B`, whereas with `Block.create(tpe)` you only know what is
  * encoded in the `BlockType tpe`.
  * For instance, if `tpe = BlockType[?]`, then you only know that
  * you will get a block, and you only have access to the methods defined in `Block`.
  *
  * ## Registering a simple block type
  * You can define a simple block that have no state like this:
  * ```
  * object Stone extends Block
  * ```
  * Here, `Stone` is an object, which means that it has only one instance: there
  * is always one and only one value of type `Stone`. This is good because, since
  * `Stone` has no state (contains no data), every block of stone is the same.
  *
  * To register our `Stone`, simply use the `register` method:
  * ```
  * Block.register(Stone)
  * ```
  *
  * ## Registering an advanced block type
  * If you want your block to contain some internal data, like a lamp,
  * declare it as a `class` instead of an `object`.
  * ```
  * class Lamp(initialState: Boolean) extends Block {
  *   private var isOn = initialState
  *   ...
  * }
  * ```
  * This time, multiple instances of `Lamp` can exist: one per lamp in the game world.
  * Therefore we use a different method to register our type of block:
  * ```
  * Block.registerStateful(() => Lamp(false))
  * ```
  * Here, we can't provide a single instance of `Lamp` because the instances of `Lamp`
  * will be created/destroyed as needed, when a lamp is placed/removed by the player.
  * What we do provide is a function that creates new lamps on demand.
  */
object Block extends TypeRegistry[Block, BlockType] {
  override protected def newType[B <: Block](provider: ()=>B, tag: ClassTag[B], id: Int) = BlockType(provider, tag, id)
}

/** Represents the game type of a block. */
final class BlockType[B <: Block](build: ()=>B, tag: ClassTag[B], id: Int) extends RegisteredType[B](build, tag, id)
