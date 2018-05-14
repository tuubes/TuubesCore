package org.tuubes.core.worlds

import org.tuubes.core.engine.GameObject

import scala.collection.mutable

/**
 * A 16*16*16 world chunk.
 * == Blocks ==
 * Each chunk stores 16*16*16 = 4096 block types, plus one light level and one skylight level for
 * each block. Each block can be associated to at most one GameObject (that's the Minecraft's
 * "tile entities").
 *
 * == GameObjects ==
 * The game objects may be associated to their chunk (if they have a physical existence in the
 * game world and/or if they need to be sent to the clients). This is useful to send the objects
 * to the game client and to detect object/object and object/block collisions.
 */
final class Chunk {
  val blocks = new ChunkBlocks()
  val blockObjects = new ChunkBlockObjects(1)
  private[tuubes] val objects = new mutable.ArrayBuffer[GameObject] // for collisions and other interactions

  val objectItr: Iterable[GameObject] = objects
  val objectCount: Int = objects.size
}
