package org.tuubes.core.worlds

import org.tuubes.core.engine.ActorMessage

sealed trait ChunkServiceMessages extends ActorMessage
final case class RequestCreate(cx: Int, cy: Int, cz: Int, callback: Chunk => ()) extends ChunkServiceMessages
final case class RequestExisting(cx: Int, cy: Int, cz: Int, callback: Option[Chunk] => ()) extends ChunkServiceMessages
final case class TestExists(cx: Int, cy: Int, cz: Int, callback: Boolean => ()) extends ChunkServiceMessages
final case class ChunkLoaded(key: Long, chunk: Chunk) extends ChunkServiceMessages
final case class ColumnLoaded(cx: Int, cz: Int, column: ChunkColumn) extends ChunkServiceMessages
