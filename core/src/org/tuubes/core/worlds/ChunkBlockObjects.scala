package org.tuubes.core.worlds

import org.tuubes.core.engine.GameObject

import scala.collection.mutable

final class ChunkBlockObjects(initialSize: Int) {
  private[this] val map3d = new mutable.LongMap[GameObject](initialSize)

  def apply(x: Int, y: Int, z: Int): Option[GameObject] = {
    map3d.get(y * 256 | z * 16 | x)
  }

  def update(x: Int, y: Int, z: Int, value: GameObject): Unit = {
    map3d(y * 256 | z * 16 | x) = value
  }
}
