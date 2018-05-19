package org.tuubes.core.worlds

/** Serves chunks from local files */
final class LocalChunkService(private val w: LocalWorld) extends ChunkService {
  private val loadedChunks = new mutable.LongMap[Chunk]()
  private val dataFolder = _ // TODO

  override def requestCreate(cx: Int, cy: Int, cz: Int, callback: Chunk => ()): Unit = {
    val loaded = loadedChunks.get(key(cz, cy, cz))
    loaded match {
      case Some(chunk) => callback(chunk)
      case None => // TODO
    }
  }

  override def requestExisting(cx: Int, cy: Int, cz: Int, callback: Option[Chunk] => ()): Unit = {
    val loaded = loadedChunks.get(key(cz, cy, cz))
    loaded match {
      case s: Some[Chunk] => callback(s)
      case None => // TODO
    }
  }


  override def testExists(cx: Int, cy: Int, cz: Int, callback: Boolean => ()): Unit = {

  }

  private def key(cx: Int, cy: Int, cz: Int): Long = {
    (cy << 60) | ((cx & 0x7FFFFFFF) << 29) | (cz & 0x7FFFFFFF)
  }
}