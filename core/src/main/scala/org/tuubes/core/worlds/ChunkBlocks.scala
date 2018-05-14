package org.tuubes.core.worlds

import com.electronwill.niol.{NiolInput, NiolOutput}
import com.electronwill.utils.CompactStorage
import org.tuubes.core.blocks.BlockType

/** 16*16*16 blocks */
final class ChunkBlocks(private[this] val storage: CompactStorage = CompactStorage(8, 4096)) {
  def apply(x: Int, y: Int, z: Int): BlockType = {
    BlockType.getOrNull(rawGet(x, y, z))
  }

  def update(x: Int, y: Int, z: Int, value: BlockType): Unit = {
    rawSet(x, y, z, value.internalId)
  }

  private[tuubes] def rawGet(x: Int, y: Int, z: Int): Int = {
    storage(y * 256 | z * 16 | x)
  }

  private[tuubes] def rawSet(x: Int, y: Int, z: Int, value: Int): Unit = {
    storage(y * 256 | z * 16 | x) = value
  }

  def write(out: NiolOutput): Unit = {
    out.putShort(storage.bitsPerValue)
    out.putInt(storage.byteSize)
    out.putBytes(storage.bytes)
  }

  def writeBytes(out: NiolOutput): Unit = {
    out.putBytes(storage.bytes)
  }
}

object ChunkBlocks {
  def read(in: NiolInput): ChunkBlocks = {
    val bitsPerValue = in.getShort()
    val byteSize = in.getInt()
    val bytes = in.getBytes(byteSize)
    val storage = CompactStorage(bitsPerValue, bytes)
    new ChunkBlocks(storage)
  }
}
