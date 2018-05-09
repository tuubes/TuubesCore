package org.tuubes.core.blocks

import com.electronwill.utils.Vec3i
import org.tuubes.core.engine.{Actor, ActorMessage}

/**
 * @author TheElectronWill
 */
sealed trait AreaMessage extends ActorMessage {}
final case class SetBlockType(x: Int, y: Int, z: Int, t: BlockType) extends AreaMessage
final case class GetBlockType(x: Int, y: Int, z: Int, replyTo: Actor) extends AreaMessage
final case class ReplyBlockType(x: Int, y: Int, z: Int, t: BlockType) extends AreaMessage

final case class ReplaceBlocks(replace: Array[BlockType], replacements: Array[BlockType])
    extends AreaMessage

final case class ReplaceBlocksAdvanced(fromX: Int,
                                       fromY: Int,
                                       fromZ: Int,
                                       toX: Int,
                                       toY: Int,
                                       toZ: Int,
                                       replace: Array[BlockType],
                                       replacements: Array[BlockType])
    extends AreaMessage {

  def this(from: Vec3i, to: Vec3i, replace: Array[BlockType], replacements: Array[BlockType]) = {
    this(from.x, from.y, from.z, to.x, to.y, to.z, replace, replacements)
  }
}

final case class FillBlocks(fillWith: BlockType, exceptions: Array[BlockType]) extends AreaMessage

final case class FillBlocksAdvanced(fromX: Int,
                                    fromY: Int,
                                    fromZ: Int,
                                    toX: Int,
                                    toY: Int,
                                    toZ: Int,
                                    fillWith: BlockType,
                                    exceptions: Array[BlockType])
    extends AreaMessage {

  def this(from: Vec3i, to: Vec3i, fillWith: BlockType, exceptions: Array[BlockType]) = {
    this(from.x, from.y, from.z, to.x, to.y, to.z, fillWith, exceptions)
  }
}
