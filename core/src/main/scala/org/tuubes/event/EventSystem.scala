package org.tuubes.event

import java.util.concurrent.ConcurrentHashMap

import com.electronwill.collections.{ConcurrentRecyclingIndex, Index, IndexRegistration}
import org.tuubes.runtime.{ExecutionGroup, TaskSystem}
import org.tuubes.world.World

import scala.collection.JavaConverters._
import scala.collection.concurrent

/**
 * @author TheElectronWill
 */
class EventSystem[I](private[this] val i: I) {
	def notify(e: Event)(implicit callerGroup: ExecutionGroup): Unit = {
		val eventClass = e.getClass
		for (l <- blockingListeners(eventClass).valuesIterator) {
			l.onEvent(e)(callerGroup, i)
		}
		for (l <- afterListeners(eventClass).valuesIterator) {
			TaskSystem.execute(() => l.onEvent(e)(null, i))
		}
	}

	def listen[E <: Event](mode: ListenMode, eventClass: Class[E],
						   listener: EventListener[E, I]): IndexRegistration = {
		val container = mode match {
			case ListenMode.BLOCKING =>
				blockingListeners.getOrElseUpdate(eventClass,
					new ConcurrentRecyclingIndex[EventListener[Event, I]])
			case ListenMode.AFTER =>
				afterListeners.getOrElseUpdate(eventClass,
					new ConcurrentRecyclingIndex[EventListener[Event, I]])
		}
		val id = container += listener.asInstanceOf[EventListener[Event, I]]
		new IndexRegistration(container, id)
	}

	private[this] val blockingListeners: concurrent.Map[Class[_ <: Event],
		Index[EventListener[Event, I]]] = new ConcurrentHashMap().asScala

	private[this] val afterListeners: concurrent.Map[Class[_ <: Event],
		Index[EventListener[Event, I]]] = new ConcurrentHashMap().asScala
}

object EventSystem {
	def apply(implicit w: World): EventSystem[World] = w.eventSystem

	val global: EventSystem[Unit] = new EventSystem[Unit](())
}