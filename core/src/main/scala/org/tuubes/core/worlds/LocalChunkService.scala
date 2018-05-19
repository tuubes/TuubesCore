package org.tuubes.core.worlds

/** Serves chunks from local files */
final class LocalChunkService(private val w: World) extends ChunkService {
  // TODO
  override def requestCreate(cx: Int, cy: Int, cz: Int, callback: Chunk => ()): Unit = {

  }

  override def requestExisting(cx: Int, cy: Int, cz: Int, callback: Option[Chunk] => ()): Unit = {

  }


  override def testExists(cx: Int, cy: Int, cz: Int, callback: Boolean => ()): Unit = {

  }
}