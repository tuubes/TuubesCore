package org.tuubes.entity

/** Base trait for entities. */
trait Entity extends Updatable {
  def world: World
  def coords: Vec3d
  def destroy(): Unit
}

/** Entity companion object and registry. */
object Entity extends TypeRegistry[Entity, EntityType] {
  override protected def newType[E <: Entity](provider: ()=>E, tag: ClassTag[E], id: Int) = EntityType(provider, tag, id)
}

/** Represents the game type of an entity. */
final class EntityType[E <: Entity](build: ()=>E, tag: ClassTag[E], id: Int) extends RegisteredType[E](build, tag, id)

private[tuubes] class LocalEntity extends Entity {
  def id: Int
}

trait Damageable {
  def damage(t: DamageType, x: Int, damager: Entity): Unit
}

trait Mountable {
  def mount(mounter: Entity): Unit
}

trait Pickupable {
  def pickup(by: Entity): Unit
}

trait Feedable {
  def feed(food: ItemType, amount: Int): Unit
}

trait Tameable {
  def tame(by: Entity): Task[Boolean]
  def sitDown(): Unit
  def standUp(): Unit
}

