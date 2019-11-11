package org.tuubes

import com.electronwill.util.Vec3i

/** A (locally managed) game world. */
final class World(val name: String) {

  /** Gets a block by its position. */
  def apply(x: Int, y: Int, z: Int): Block
  
  /** Gets a block by its position. */
  def apply(pos: Vec3i) = apply(pos.x, pos.y, pos.z)
  
  /** Puts a block at some position, replacing any existing block. */
  def put(x: Int, y: Int, z: Int, b: Block): Unit
  
  /** Puts a block at some position, replacing any existing block. */
  def put(pos: Vec3i, b: Block) = put(pos.x, pos.y, pos.z, b)
}

object World {
  /** Returns an existing world */
  def apply(name: String): World
  
  /** Returns a world if it exists, or None */
  def get(name: String): Option[World]
  
  /** Creates a new game world */
  def create(name: String): World
}

