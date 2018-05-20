package org.tuubes.core.worlds

/** Generates chunks */
trait ChunkGenerator {
  def generateColumn(cx: Int, cz: Int): Array[Chunk]
}
