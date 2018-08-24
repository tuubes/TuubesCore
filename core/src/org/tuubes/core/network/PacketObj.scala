package org.tuubes.core.network

import com.electronwill.collections.ConcurrentRecyclingIndex
import com.electronwill.niol.Reader
import com.electronwill.niol.network.tcp.ClientAttach
import org.tuubes.core.TuubesServer
import org.tuubes.core.engine._

/** Trait for packet companion objects */
abstract class PacketObj[C <: ClientAttach, P <: Packet] extends BiObservable[C, P, C] with Reader[P] {
  /** The packet's id */
  val id: Int

  /** Handles the packet */
  def handle(packet: P, client: C): Unit = {
    TuubesServer.logger.debug(s"Received packet ${packet.getClass}")
    for (observer <- observers.valuesIterator) {
      observer.onEvent(packet, client)
    }
  }

  private[this] val observers = new ConcurrentRecyclingIndex[BiObserver[P, C]]()

  override def subscribe(observer: BiObserver[P, C]): Registration[C] = {
    val key = new ListenKey[C](observers += observer)
    new ListenRegistration[C](key, k => observers.remove(k.id))
  }
}
