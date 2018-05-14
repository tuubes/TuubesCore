package org.tuubes.core

package object worlds {
  val MaxVerticalChunks: Int = 16 // maxHeight = 256
  val MaxHeight: Int = 16 * MaxVerticalChunks

  type ChunkColumn = Array[Chunk]
}
