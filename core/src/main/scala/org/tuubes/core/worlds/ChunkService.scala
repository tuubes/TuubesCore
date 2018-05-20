package org.tuubes.core.worlds

import org.tuubes.core.engine.ExecutionGroup

/**
 * Asynchronously provides Chunk objects for one world.
 */
trait ChunkService {
  /**
   * Requests a chunk of 16x16x16 blocks. If the chunk doesn't exist it will be created.
   *
   * @param cx: chunk X coordinate
   * @param cy: chunk Y coordinate
   * @param cz: chunk Z coordinate
   * @param callback: the function to call when the chunk is available
   */
  def requestCreate(cx: Int, cy: Int, cz: Int, callback: Chunk => ())
                   (implicit currentGroup: ExecutionGroup): Unit

  /**
   * Requests a chunk of 16x16x16 blocks. If the chunk doesn't exist then you'll get None.
   *
   * @param cx: chunk X coordinate
   * @param cy: chunk Y coordinate
   * @param cz: chunk Z coordinate
   * @param callback: the function to call when the result is available
   */
  def requestExisting(cx: Int, cy: Int, cz: Int, callback: Option[Chunk] => ())
                     (implicit currentGroup: ExecutionGroup): Unit

  /**
   * Tests if a chunk exists.
   *
   * @param cx: chunk X coordinate
   * @param cy: chunk Y coordinate
   * @param cz: chunk Z coordinate
   * @param callback: the function to call when the result is available
   */
  def testExists(cx: Int, cy: Int, cz: Int, callback: Boolean => ())
                (implicit currentGroup: ExecutionGroup): Unit
}
