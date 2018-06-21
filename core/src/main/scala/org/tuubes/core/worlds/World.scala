package org.tuubes.core.worlds

/** A game world */
trait World {
  def name: String
  def chunkProvider: ChunkService
  def chunkGenerator: ChunkGenerator
}
