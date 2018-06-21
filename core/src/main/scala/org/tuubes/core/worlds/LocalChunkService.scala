package org.tuubes.core.worlds

import java.nio.file.StandardOpenOption

import better.files.File
import com.electronwill.collections.{Bag, SimpleBag}
import com.electronwill.niol.io.ChannelInput
import org.tuubes.core.TuubesServer
import org.tuubes.core.engine.{ActorMessage, ExecutionGroup, LocalActor}
import org.tuubes.core.tasks.{IOSystem, TaskSystem}

import scala.collection.mutable

/**
 * Asynchronously servers chunk columns from local files.
 *
 * @param world the world that this service takes care of
 */
final class LocalChunkService(private val world: LocalWorld) extends LocalActor with ChunkService {
  /** The currently loaded chunk columns */
  private val loadedColumns = new mutable.LongMap[ChunkColumn]()

  /** Chunks that are being loaded asynchronously */
  private val loading = new mutable.LongMap[Bag[ChunkColumn => Unit]]()

  /** Chunks that are being generated asynchronously */
  private val generating = new mutable.LongMap[Bag[ChunkColumn => Unit]]()

  /** The directory that stores the chunks data */
  private val chunksDir = world.directory / "chunks"

  /** Packs 2 ints into 1 long. */
  private def key(cx: Int, cz: Int): Long = {
    cx.toLong << 32 | cz & 0xFFFFFFFFl
  }

  /** The chunk column file */
  private def file(cx: Int, cz: Int): File = {
    chunksDir / s"$cx,$cz.chunkcol"
  }

  // --- Actor ---
  override protected def filter(msg: ActorMessage): Boolean = {
    super.filter(msg) && msg.isInstanceOf[ChunkServiceMessage]
  }

  override def update(dt: Double): Unit = {
    // TODO clean old chunks? autosave?
  }

  override protected def onMessage(msg: ActorMessage): Unit = {
    super.onMessage(msg)
    msg match {
      case RequestCreate(cx, cz, callback) => processReqCreate(cx, cz, callback)
      case RequestExisting(cx, cz, callback) => processReqExisting(cx, cz, callback)
      case TestExists(cx, cz, callback) => processTestExists(cx, cz, callback)
      case LoadComplete(key, column) => {
        loadedColumns(key) = column
        loading.remove(key).foreach(_.foreach(_ (column))) // remove the callbacks bag and call them
      }
      case GenerationComplete(key, column) => {
        loadedColumns(key) = column
        generating.remove(key).foreach(_.foreach(_ (column))) // remove the callbacks bag and call them
      }
    }
  }

  // --- Actual processing ---
  private def processReqCreate(cx: Int, cz: Int, callback: ChunkColumn => Unit): Unit = {
    val columnKey = key(cx, cz)
    val loaded = loadedColumns.get(columnKey)
    loaded match {
      case Some(chunk) => {
        // The chunk is loaded => callback now
        callback(chunk)
      }
      case None => {
        // The chunk isn't loaded
        val chunkFile = file(cx, cz)
        if (chunkFile.exists) {
          // Loads the chunk if it's not already being loaded
          asyncLoad(chunkFile, callback, columnKey)
        } else {
          // Generates the chunk if it's not already being generated
          asyncGen(cx, cz, callback, columnKey)
        }
      }
    }
  }

  private def processReqExisting(cx: Int, cz: Int, callback: Option[ChunkColumn] => Unit): Unit = {
    val columnKey = key(cx, cz)
    val loaded = loadedColumns.get(columnKey)
    loaded match {
      case s: Some[Chunk] => callback(s)
      case None => {
        val chunkFile = file(cx, cz)
        if (chunkFile.exists) {
          // Loads the chunk if it's not already being loaded
          asyncLoad(chunkFile, chunk => callback(Some(chunk)), columnKey)
        } else {
          callback(None)
        }
      }
    }
  }

  private def processTestExists(cx: Int, cz: Int, callback: Boolean => Unit): Unit = {
    val loaded = loadedColumns.get(key(cx, cz))
    loaded match {
      case Some(_) => callback(true)
      case None => callback(file(cx, cz).exists)
    }
  }

  private def asyncLoad(file: File, callback: ChunkColumn => Unit, key: Long): Unit = {
    loading.get(key) match {
      case Some(bag) => bag += callback // registers the callback
      case None => {
        // Create a list of callbacks and registers the callback
        val newBag = new SimpleBag[ChunkColumn => Unit](1)
        newBag += callback
        // Marks the chunk column as "loading"
        loading(key) = newBag
        // Loads the chunk column
        IOSystem.execute(() => {
          for (channel <- file.fileChannel(Seq(StandardOpenOption.READ))) {
            val input = new ChannelInput(channel)
            val column = ChunkColumn.read(input)
            handleLater(LoadComplete(key, column))
          }
        }, TuubesServer.logger.error(s"Unable to read chunk from $file", _))
      }
    }
  }

  private def asyncGen(cx: Int, cz: Int, callback: ChunkColumn => Unit, key: Long): Unit = {
    generating.get(key) match {
      case Some(bag) => bag += callback // registers the callback
      case None => {
        // Create a list of callbacks and registers the callback
        val newBag = new SimpleBag[ChunkColumn => Unit](1)
        newBag += callback
        // Marks the chunk column as "generating"
        generating(key) = newBag
        // Generates the chunk column
        TaskSystem.execute(() => {
          val column = world.chunkGenerator.generate(cz, cz)
          handleLater(GenerationComplete(key, column))
        })
      }
    }
  }

  // --- ChunkService methods ---
  override def requestCreate(cx: Int, cz: Int, callback: ChunkColumn => Unit)
                            (implicit currentGroup: ExecutionGroup): Unit = {
    if (currentGroup eq group) {
      processReqCreate(cx, cz, callback) // avoids creating a message in that case
    } else {
      handleLater(RequestCreate(cx, cz, callback))
    }
  }

  override def requestExisting(cx: Int, cz: Int, callback: Option[ChunkColumn] => Unit)
                              (implicit currentGroup: ExecutionGroup): Unit = {
    if (currentGroup eq group) {
      processReqExisting(cx, cz, callback) // avoids creating a message in that case
    } else {
      handleLater(RequestExisting(cx, cz, callback))
    }
  }


  override def testExists(cx: Int, cz: Int, callback: Boolean => Unit)
                         (implicit currentGroup: ExecutionGroup): Unit = {
    if (currentGroup eq group) {
      processTestExists(cx, cz, callback) // avoids creating a message in that case
    } else {
      handleLater(TestExists(cx, cz, callback))
    }
  }
}
