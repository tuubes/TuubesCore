package org.tuubes.core.worlds

import org.tuubes.core.engine.ActorMessage

sealed trait ChunkServiceMessage extends ActorMessage

final case class RequestCreate(cx: Int, cz: Int, callback: ChunkColumn => Unit)
  extends ChunkServiceMessage

final case class RequestExisting(cx: Int, cz: Int, callback: Option[ChunkColumn] => Unit)
  extends ChunkServiceMessage

final case class TestExists(cx: Int, cz: Int, callback: Boolean => Unit)
  extends ChunkServiceMessage

final case class LoadComplete(key: Long, column: ChunkColumn)
  extends ChunkServiceMessage

final case class GenerationComplete(key: Long, column: ChunkColumn)
  extends ChunkServiceMessage
