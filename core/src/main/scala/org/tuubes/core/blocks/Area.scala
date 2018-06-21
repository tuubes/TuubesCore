package org.tuubes.core.blocks

import org.tuubes.core.engine.messages.EngineMessage
import org.tuubes.core.engine.{Actor, ActorMessage}

/**
 * @author TheElectronWill
 */
trait Area extends Actor {
  def filter(msg: ActorMessage): Boolean = {
    msg.isInstanceOf[AreaMessage] || msg.isInstanceOf[EngineMessage]
  }
}
