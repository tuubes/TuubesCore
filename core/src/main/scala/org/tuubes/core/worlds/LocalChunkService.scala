package org.tuubes.core.worlds

import java.nio.file.StandardOpenOption

import better.files.File
import com.electronwill.niol.io.ChannelInput
import org.tuubes.core.engine.{ActorMessage, ExecutionGroup, LocalActor}
import org.tuubes.core.tasks.AsyncTask

import scala.collection.mutable

/** Serves chunks from local files */
final class LocalChunkService(private val w: LocalWorld) extends LocalActor with ChunkService {
  private val loadedChunks = new mutable.LongMap[Chunk]()
  private val loadingChunks = new mutable.HashSet[Long]()
  private val chunksDir = w.directory / "chunks"

  // --- ChunkService methods ---
  override def requestCreate(cx: Int, cy: Int, cz: Int, callback: Chunk => Unit)
                            (implicit currentGroup: ExecutionGroup): Unit = {
    if (currentGroup eq group) {
      processReqCreate(cx, cy, cz, callback) // avoids creating a message in that case
    } else {
      handleLater(RequestCreate(cx, cy, cz, callback))
    }
  }

  override def requestExisting(cx: Int, cy: Int, cz: Int, callback: Option[Chunk] => Unit)
                              (implicit currentGroup: ExecutionGroup): Unit = {
    if (currentGroup eq group) {
      processReqExisting(cz, cy, cz, callback) // avoids creating a message in that case
    } else {
      handleLater(RequestExisting(cz, cy, cz, callback))
    }
  }


  override def testExists(cx: Int, cy: Int, cz: Int, callback: Boolean => Unit)
                         (implicit currentGroup: ExecutionGroup): Unit = {
    if (currentGroup eq group) {
      processTestExists(cz, cy, cz, callback) // avoids creating a message in that case
    } else {
      handleLater(TestExists(cz, cy, cz, callback))
    }
  }

  // --- Actor ---
  override def update(dt: Double): Unit = {} // TODO clean old chunks? autosave?

  override protected def onMessage(msg: ActorMessage): Unit = {
    super.onMessage(msg)
    msg match {
      case RequestCreate(cx, cy, cz, callback) => processReqCreate(cx, cy, cz, callback)
      case RequestExisting(cx, cy, cz, callback) => processReqExisting(cx, cy, cz, callback)
      case TestExists(cx, cy, cz, callback) => processTestExists(cx, cy, cz, callback)
      case ChunkLoaded(key, chunk) => {
        loadedChunks(key) = chunk
        loadingChunks.remove(key)
      }
    }
  }

  // --- Actual processing ---
  private def processReqCreate(cx: Int, cy: Int, cz: Int, callback: Chunk => Unit): Unit = {
    val xyzKey = key(cx, cy, cz)
    val loaded = loadedChunks.get(xyzKey)
    loaded match {
      case Some(chunk) => callback(chunk)
      case None => {
        val chunkFile = file(cx, cy, cz)
        if (chunkFile.exists && !loadingChunks.contains(xyzKey)) {
          load(chunkFile, callback, xyzKey)
        } else {
          // TODO generate the chunk, async
        }
      }
    }
  }

  private def processReqExisting(cx: Int, cy: Int, cz: Int, callback: Option[Chunk] => Unit): Unit = {
    val xyzKey = key(cx, cy, cz)
    val loaded = loadedChunks.get(xyzKey)
    loaded match {
      case s: Some[Chunk] => callback(s)
      case None => {
        val chunkFile = file(cx, cy, cz)
        if (chunkFile.exists && !loadingChunks.contains(xyzKey)) {
          load(chunkFile, chunk => callback(Some(chunk)), xyzKey)
        } else {
          callback(None)
        }
      }
    }
  }

  private def processTestExists(cx: Int, cy: Int, cz: Int, callback: Boolean => Unit): Unit = {
    val loaded = loadedChunks.get(key(cz, cy, cz))
    loaded match {
      case Some(_) => callback(true)
      case None => callback(file(cx, cy, cz).exists)
    }
  }

  private def key(cx: Int, cy: Int, cz: Int): Long = {
    (cy << 60) | ((cx & 0x7FFFFFFF) << 29) | (cz & 0x7FFFFFFF)
  }

  private def file(cx: Int, cy: Int, cz: Int): File = chunksDir / s"$cx,$cy,$cz.chunk"

  private def load(file: File, callback: Chunk => Unit, key: Long): Unit = {
    loadingChunks.add(key)
    AsyncTask.io(() => {
      for (channel <- file.fileChannel(Seq(StandardOpenOption.READ))) {
        val input = new ChannelInput(channel)
        val blocks = ChunkBlocks.read(input)
        val chunk = new Chunk(blocks)
        handleLater(ChunkLoaded(key, chunk))
        callback(chunk)
      }
      null
    }).start()
  }
}
