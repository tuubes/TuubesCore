package org.tuubes.core.worlds

import better.files.File
import org.tuubes.core.TuubesServer

/**
 * A local world.
 *
 * @author TheElectronWill
 */
class LocalWorld(val name: String) extends World {
  override def chunkProvider: ChunkService = new LocalChunkService(this)

  val directory: File = TuubesServer.DirWorlds / name
}
