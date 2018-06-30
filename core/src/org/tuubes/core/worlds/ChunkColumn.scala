package org.tuubes.core.worlds

import com.electronwill.niol.{NiolInput, NiolOutput}

/**
 * A column of chunks.
 *
 * @param chunks the chunks
 * @param biomes the biomes, indexes being z*16+x
 */
final class ChunkColumn(val chunks: Array[Chunk] = Array.fill(MaxVerticalChunks) {new Chunk()},
                        val biomes: Array[BiomeType] = new Array(256)) {
  def getBiome(x: Int, z: Int): BiomeType = biomes(z * 16 + x)

  def setBiome(x: Int, z: Int, b: BiomeType): Unit = biomes(z * 16 + x) = b

  def write(out: NiolOutput): Unit = {
    out.putByte(chunks.length)
    for (chunk <- chunks) {
      chunk.blocks.write(out)
    }
    for (biome <- biomes) {
      out.putByte(biome.internalId)
    }
  }
}

/** ChunkColumn companion object with utility methods */
object ChunkColumn {
  def read(in: NiolInput): ChunkColumn = {
    val nChunks = in.getUnsignedByte()
    val chunks = new Array[Chunk](nChunks)
    for (i <- 0 to MaxVerticalChunks) {
      val blocks = ChunkBlocks.read(in)
      chunks(i) = new Chunk(blocks)
    }
    val biomes = new Array[BiomeType](256)
    for (i <- 0 until 256) {
      val id = in.getUnsignedByte()
      val biomeType = BiomeType.getOrNull(id)
      biomes(i) = biomeType
    }
    new ChunkColumn(chunks, biomes)
  }
}
