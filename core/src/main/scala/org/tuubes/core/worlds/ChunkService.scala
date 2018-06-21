package org.tuubes.core.worlds

import org.tuubes.core.engine.ExecutionGroup

/**
 * Asynchronously provides ChunkColumn objects for one world.
 */
trait ChunkService {
  /**
   * Requests a chunk column of `MaxVerticalChunks` chunks.
   * If the column doesn't exist it will be created.
   *
   * @param cx       : column X coordinate
   * @param cz       : column Z coordinate
   * @param callback : the function to call when the column is available
   */
  def requestCreate(cx: Int, cz: Int, callback: ChunkColumn => Unit)
                   (implicit currentGroup: ExecutionGroup): Unit

  /**
   * Requests a chunk column of `MaxVerticalChunks` chunks.
   * If the column doesn't exist then you'll get None.
   *
   * @param cx       : column X coordinate
   * @param cz       : column Z coordinate
   * @param callback : the function to call when the result is available
   */
  def requestExisting(cx: Int, cz: Int, callback: Option[ChunkColumn] => Unit)
                      (implicit currentGroup: ExecutionGroup): Unit

  /**
   * Tests if a chunk column exists.
   *
   * @param cx       : column X coordinate
   * @param cz       : column Z coordinate
   * @param callback : the function to call when the result is available
   */
  def testExists(cx: Int, cz: Int, callback: Boolean => Unit)
                (implicit currentGroup: ExecutionGroup): Unit
}
