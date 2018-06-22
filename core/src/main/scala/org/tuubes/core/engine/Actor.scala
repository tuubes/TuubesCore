package org.tuubes.core.engine

/**
 * An actor is a basic isolated object that reacts to the messages it receives. The only way to
 * interact with an actor is to send it an asynchronous [[ActorMessage]]. An actor may be local
 * or remote, it doesn't change the way we interact with it.
 *
 * @author TheElectronWill
 */
trait Actor {

  /**
	 * Sends a message to this actor.
	 *
	 * @param msg the message
	 */
  def !(msg: ActorMessage)(implicit currentGroup: ExecutionGroup): Unit
}
