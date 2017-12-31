package org.tuubes.core.engine

/**
 * An actor is a basic isolated "entity" that reacts to the messages it receives. The only way to
 * interact with an actor is to send it an [[ActorMessage]].
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

	/**
	 * Reacts to a received message.
	 *
	 * @param msg the received message
	 */
	protected def onMessage(msg: ActorMessage)

	/**
	 * Filters a message before it is sent to this actor. Returns true if and only if the message
	 * can be sent, false if it must be ignored. An ignored message will never be processed by
	 * the  [[onMessage()]] method.
	 *
	 * @param msg the message that is going to be sent
	 * @return true if the message can be sent, false to ignore the message
	 */
	protected def filter(msg: ActorMessage): Boolean

	/**
	 * Gets the ID that uniquely identifies this actor.
	 *
	 * @return the actor's unique id
	 */
	def id: ActorId
}