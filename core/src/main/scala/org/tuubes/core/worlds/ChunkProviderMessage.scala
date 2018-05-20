package org.tuubes.core.worlds

import org.tuubes.core.engine.ActorMessage

sealed trait ChunkProviderMessage extends ActorMessage
final case class RequestCreate(cx: Int, cy: Int, cz: Int, callback: Chunk => ()) extends ChunkProviderMessage
final case class RequestExisting(cx: Int, cy: Int, cz: Int, callback: Option[Chunk] => ()) extends ChunkProviderMessage
final case class TestExists(cx: Int, cy: Int, cz: Int, callback: Boolean => ()) extends ChunkProviderMessage
