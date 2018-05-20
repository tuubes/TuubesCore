package org.tuubes.core.worlds

import java.util.Random

import org.tuubes.core.blocks.BlockType

/** Generates simple chunks with a pseudo-randon noisy heightmap */
class BasicHeightmapChunkGenerator extends ChunkGenerator {
  private val noise = new SimplexNoise(new Random())
  private val seaLevel = 70
  private val minHeight = 30
  private val maxHeight = 200
  private val noiseFactor = 1 / 50.0

  private val stoneBlock = BlockType("stone").get
  private val grassBlock = BlockType("grass").get
  private val sandBlock = BlockType("sand").get
  private val waterBlock = BlockType("water").get

  override def generateColumn(cx: Int, cz: Int): Array[Chunk] = {
    val column = Array.fill(16) {new Chunk()}
    for (x <- 0 to 15) {
      val blockX = cx * 16 + x
      val noiseX = blockX * noiseFactor
      for (z <- 0 to 15) {
        val blockZ = cx * 16 + z
        val noiseZ = blockZ * noiseFactor
        val noiseValue = noise.generate(noiseX, noiseZ) // in range [-1,1]
        val normalized = (noiseValue + 1.0) / 2.0 // in range [0,1]
        val height = (normalized * (maxHeight - minHeight) + minHeight).toInt // in range [minHeight, maxHeight]
        gen(x, z, height, column)
      }
    }
    column
  }

  private def gen(x: Int, z: Int, height: Int, chunks: Array[Chunk]): Unit = {
    for (y <- 0 until height) { // ground
      chunks(y / 16).blocks(x, y % 16, z) = stoneBlock
    }
    if (height > seaLevel) { // land top
      chunks(height / 16).blocks(x, height % 16, z) = grassBlock
    } else { // ocean
      chunks(height / 16).blocks(x, height % 16, z) = sandBlock
      for (y <- height to seaLevel) {
        chunks(y / 16).blocks(x, y % 16, z) = waterBlock
      }
    }
  }
}
