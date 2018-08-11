package org.tuubes.core.engine

import java.util.concurrent.ConcurrentLinkedQueue

import org.tuubes.core.engine.ActorState._
import org.tuubes.core.engine.messages.Terminate

/**
 * @author TheElectronWill
 */
abstract class LocalActor extends Actor {
  protected[this] val mailBox = new ConcurrentLinkedQueue[ActorMessage]
  private[engine] var state: ActorState = Created

  override def !(msg: ActorMessage)(implicit currentGroup: ExecutionGroup): Unit = {
    if (filter(msg)) {
      mailBox.add(msg)
    }
  }

  /**
   * Reacts to a received message.
   *
   * @param msg the received message
   */
  protected def onMessage(msg: ActorMessage): Unit = {
    msg match {
      case Terminate => terminate()
    }
  }

  final def processMessages(): Unit = {
    var msg = mailBox.poll()
    while (msg != null && state == Running) {
      onMessage(msg)
      msg = mailBox.poll()
    }
  }

  def update(dt: Double): Unit

  protected[this] def terminate(): Unit = {
    state = Terminated
  }

  /**
   * Filters a message before it is sent to this actor. Returns true if and only if the message
   * can be sent, false if it must be ignored. An ignored message will never be processed by
   * the  [[onMessage()]] method.
   *
   * @param msg the message that is going to be sent
   * @return true if the message can be sent, false to ignore the message
   */
  protected def filter(msg: ActorMessage): Boolean = {
    state != Terminated
  }
}
