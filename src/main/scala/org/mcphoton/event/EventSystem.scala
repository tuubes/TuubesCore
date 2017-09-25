package org.mcphoton.event

import java.util.concurrent.ConcurrentHashMap

import org.mcphoton.world.World

import scala.collection.{concurrent, mutable}
import scala.collection.JavaConverters._

/**
 * @author TheElectronWill
 */
class EventSystem[I] {
	def notify(e: Event): Unit = {
		val eventClass = e.getClass
		for (l: EventListener[Event, I] <- blockingListeners(eventClass)) {
			l.onEvent(e)
		}
		for (l <- afterListeners(eventClass)) {
			//TODO TaskSystem.submit(()=>l.onEvent(e))
		}
	}
	def listen[E <: Event](mode: ListenMode, eventClass: Class[E],
						   listener: EventListener[E, I]): ListenKey[E, I] = {
		val container = mode match {
			case ListenMode.BLOCKING =>
				blockingListeners.getOrElseUpdate(eventClass, new mutable.HashSet())
			case ListenMode.AFTER =>
				afterListeners.getOrElseUpdate(eventClass, new mutable.HashSet())
		}
		container += listener
		new ListenKey[E, I](container, listener)
	}

	private[this] val blockingListeners: concurrent.Map[Class[_ <: Event],
		mutable.Set[EventListener[_, I]]] = new ConcurrentHashMap().asScala

	private[this] val afterListeners: concurrent.Map[Class[_ <: Event],
		mutable.Set[EventListener[_, I]]] = new ConcurrentHashMap().asScala
}
object EventSystem {
	def apply(implicit w: World): EventSystem[World] = w.eventSystem
	val global: EventSystem[Unit] = new EventSystem()
}