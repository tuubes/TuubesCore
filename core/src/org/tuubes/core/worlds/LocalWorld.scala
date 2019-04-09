package org.tuubes.core.worlds

import better.files.File
import com.electronwill.collection.ConcurrentRecyclingIndex
import org.tuubes.core.TuubesServer
import org.tuubes.core.engine.GameObject

/**
 * A local world.
 *
 * @author TheElectronWill
 */
final class LocalWorld(val name: String) extends World {
  override def chunkProvider: ChunkService = new LocalChunkService(this)
  override val chunkGenerator: ChunkGenerator = new BasicHeightmapChunkGenerator()

  val directory: File = TuubesServer.DirWorlds / name

  private val gameObjects = new ConcurrentRecyclingIndex[GameObject](64)

  private[tuubes] def add(obj: GameObject): Unit = {
    val id = gameObjects += obj
    obj.id = id
    obj.world = this
  }

  private[tuubes] def remove(obj: GameObject): Unit = {
    gameObjects.remove(obj.id)
  }
}
