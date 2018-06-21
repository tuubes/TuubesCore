package org.tuubes.core.worlds

/** Generates chunks */
trait ChunkGenerator {
  def generate(cx: Int, cz: Int): ChunkColumn
}
