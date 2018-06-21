package org.tuubes.core.engine

import java.util.concurrent.atomic.AtomicInteger

/**
 * A unique ID associated to an [[Actor]].
 *
 * @author TheElectronWill
 */
private[tuubes] final class ActorId(val id: Int) extends AnyVal {}
object ActorId {
  private val counter = new AtomicInteger()

  private[tuubes] def next(): ActorId = new ActorId(counter.getAndIncrement())
}
