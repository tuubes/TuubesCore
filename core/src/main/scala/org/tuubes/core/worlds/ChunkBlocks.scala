package org.tuubes.core.worlds

import com.electronwill.niol.{NiolInput, NiolOutput}
import com.electronwill.utils.CompactStorage
import org.tuubes.core.blocks.BlockType

/** 16*16*16 blocks */
final class ChunkBlocks(private val oneTypeLayers: Array[BlockType],
                        private val complexLayers: Array[CompactStorage],
                        private var palette: Bag[BlockType]) {
  def apply(x: Int, y: Int, z: Int): BlockType = {
    val oneType = oneTypeLayers(y)
    if (oneType ne null) {
      oneType
    } else {
      val layer = complexLayers(y)
      val id = layer(x * 16 + z)
      if (palette ne null) {
        palette.get(id)
      } else {
        BlockType.getOrNull(id)
      }
    }
  }

  def update(x: Int, y: Int, z: Int, value: BlockType): Unit = {
    val oneType = oneTypeLayers(y)
    if (oneType ne null) {
      if (oneType ne value) {
        if (oneType.internalId == 0) {
          // This layer is currenly empty (full of air) => simply change its type
          oneTypeLayers(y) = value
        } else {
          // Replace the one-type layer by a complex layer
          val oldId = blockInsertionId(oneType)
          val newId = blockInsertionId(value)
          val layer = CompactStorage(InitialBitsPerBlock, 256) // new layer of 16x16 = 256 values
          layer.fill(oldId) // initializes the whole layer with the old blockType
          complexLayers(i) = layer
          oneTypeLayers(i) = null
          set(layer, x, z, newId) // modifies the specified block
        }
      }
    } else {
      val id = blockInsertionId(value)
      val layer = complexLayers(y)
      set(layer, x, z, id)
    }
  }

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

  private def convertToInternalIds(layer: CompactStorage): CompactStorage = {
    val newLayer = CompactStorage(16, 256)
    var i = 0
    while (i < 256) {
      val paletteId = layer(i)
      val internalId = palette.get(paletteId).internalId
      newLayer(i) = internalId
      i += 1
    }
    newLayer
  }

  def write(out: NiolOutput): Unit = {
    out.putShort(storage.bitsPerValue)
    out.putInt(storage.byteSize)
    out.putBytes(storage.bytes)
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

object ChunkBlocks {
  val InitialBitsPerBlock = 4
  val MaxPaletteSize = 256

  def read(in: NiolInput): ChunkBlocks = {
    // Read the palette, if any
    val paletteSize = in.getShort()
    val palette = if (paletteSize == 0) null else new SimpleBag[BlockType](paletteSize)
    if (palette ne null) {
      for (i <- 0 until paletteSize) {
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
        val internalId = in.getVarint()
        oneTypeLayers(i) = BlockType.getOrNull(internalId)
      } else {
        val arraySize = CompactStorage.byteSize(bitsPerValue, 256)
        val array = in.getBytes(arraySize)
        complexLayers(i) = CompactStorage(bitsPerValue, array)
      }
    }
    new ChunkBlocks(oneTypeLayers, complexLayers, palette)
  }
}
