package org.tuubes.core.worlds

import com.electronwill.collections.{Bag, SimpleBag}
import com.electronwill.niol.{NiolInput, NiolOutput}
import com.electronwill.utils.CompactStorage
import org.tuubes.core.blocks.BlockType
import ChunkBlocks.{InitialBitsPerBlock, MaxPaletteSize}

/** 16*16*16 blocks */
final class ChunkBlocks(private val oneTypeLayers: Array[BlockType],
                        private val complexLayers: Array[CompactStorage],
                        private var palette: Bag[BlockType]) {
  /** Gets a block */
  def apply(x: Int, y: Int, z: Int): BlockType = {
    val oneType = oneTypeLayers(y)
    if (oneType ne null) {
      oneType
    } else {
      val layer = complexLayers(y)
      val id = layer(x * 16 + z)
      if (palette ne null) {
        palette(id)
      } else {
        BlockType.getOrNull(id)
      }
    }
  }

  /** Sets a block */
  def update(x: Int, y: Int, z: Int, value: BlockType): Unit = {
    val oneType = oneTypeLayers(y)
    if (oneType ne null) {
      if (oneType ne value) {
        // Replace the one-type layer by a complex layer
        val oldId = blockInsertionId(oneType)
        val newId = blockInsertionId(value)
        val layer = CompactStorage(InitialBitsPerBlock, 256) // new layer of 16x16 = 256 values
        layer.fill(oldId) // initializes the whole layer with the old blockType
        complexLayers(y) = layer
        oneTypeLayers(y) = null
        set(layer, x, z, newId) // modifies the specified block
      }
    } else {
      val id = blockInsertionId(value)
      val layer = complexLayers(y)
      set(layer, x, z, id)
    }
  }

  /**
   * Computes the id that must be used to insert this block into the chunk.
   * This method takes care of adding the block to the palette if needed. Also, it may remove the
   * palette in favor of the internalIds, if the palette's size exceeds the maximum value.
   */
  private def blockInsertionId(value: BlockType): Int = {
    // The block id, either its internalId or its id in the palette.
    if (palette eq null) {
      // There is no palette => use Tuubes' internalId
      value.internalId
    } else {
      val idInPalette = palette.indexOf(value)
      if (idInPalette != -1) {
        idInPalette
      } else {
        // This BlockType isn't in the chunk's palette
        if (palette.size < MaxPaletteSize) {
          palette += value // Add the BlockType to the palette
          palette.size - 1 // The type is added at the end of the palette
        } else {
          // The palette has reached its maximum size => remove it and use the internalId
          // First, convert all the palette ids to internalIds:
          var i = 0
          while (i < 16) {
            val layer = complexLayers(i)
            if (layer ne null) {
              complexLayers(i) = convertToInternalIds(layer)
            }
            i += 1
          }
          // Then, remove the palette:
          palette = null
          // Finally, returns the internalId of the 'value' block
          value.internalId
        }
      }
    }
  }

  /** Sets a block in the given layer, expanding the layer if needed */
  private def set(layer: CompactStorage, x: Int, z: Int, blockId: Int): Unit = {
    val diff = blockId >> layer.bitsPerValue // Bits that can't be handled by the current layer
    if (diff == 0) {
      // All right => set the block id and return
      layer(x * 16 + z) = blockId
    } else {
      // The number of bits/value in this layer is too small to handle the id => increase bits/v
      val increase = 32 - Integer.numberOfLeadingZeros(diff) // log2(diff)
      val newLayer = layer.expand(increase)
      newLayer(x * 16 + z) = blockId
    }
  }

  /** Converts a layer's ids to internal ids, by copying them into a new storage. */
  private def convertToInternalIds(layer: CompactStorage): CompactStorage = {
    val newLayer = CompactStorage(16, 256)
    var i = 0
    while (i < 256) {
      val paletteId = layer(i)
      val internalId = palette(paletteId).internalId
      newLayer(i) = internalId
      i += 1
    }
    newLayer
  }

  /** Writes a chunk that can be read by [[ChunkBlocks.read]]. The format is specific to Tuubes. */
  def write(out: NiolOutput): Unit = {
    // Write the palette, if any
    if (palette ne null) {
      out.putShort(palette.size)
      for (i <- 0 until palette.size) {
        out.putVarint(palette(i).internalId)
      }
    } else {
      out.putShort(0)
    }
    // Write the layers
    for (i <- 0 until 16) {
      val oneLayer = oneTypeLayers(i)
      if (oneLayer ne null) {
        out.putByte(0)
        out.putVarint(oneLayer.internalId)
      } else {
        val complexLayer = complexLayers(i)
        out.putByte(complexLayer.bitsPerValue)
        out.putBytes(complexLayer.bytes)
      }
    }
  }
}

/** Companion object for ChunkBlocks */
object ChunkBlocks {
  /** The number of bits per block of the new layers */
  val InitialBitsPerBlock = 4
  /**
   * The maximum size of the ID palette (which maps chunk ids to real BlockTypes).
   * If the palette gets bigger it is removed and the internal ids are used instead of the
   * palette's ids.
   */
  val MaxPaletteSize = 256

  /** Reads a chunk, as written by [[ChunkBlocks.write]]. The format is specific to Tuubes. */
  def read(in: NiolInput): ChunkBlocks = {
    // Read the palette, if any
    val paletteSize = in.getShort()
    val palette = if (paletteSize == 0) null else new SimpleBag[BlockType](paletteSize)
    if (palette ne null) {
      for (i <- 0 until paletteSize) {
        // Palette entry: paletteId = entry's position in the bag, internalId = entry's value
        val internalId = in.getVarint()
        palette += BlockType.getOrNull(internalId)
      }
    }
    // Read the layers
    val oneTypeLayers = new Array[BlockType](16)
    val complexLayers = new Array[CompactStorage](16)
    for (i <- 0 until 16) {
      val bitsPerValue = in.getByte()
      if (bitsPerValue == 0) {
        // One-type layer => read its only type
        val internalId = in.getVarint()
        oneTypeLayers(i) = BlockType.getOrNull(internalId)
      } else {
        // Complex layer => read each value
        val arraySize = CompactStorage.byteSize(bitsPerValue, 256)
        val array = in.getBytes(arraySize)
        complexLayers(i) = CompactStorage(bitsPerValue, array)
      }
    }
    new ChunkBlocks(oneTypeLayers, complexLayers, palette)
  }

  def empty: ChunkBlocks = {
    val air = BlockType.getOrNull(0)
    val oneTypeLayers = Array.fill(16)(air)
    val complexLayers = new Array[CompactStorage](16)
    new ChunkBlocks(oneTypeLayers, complexLayers, new SimpleBag[BlockType](4))
  }
}
