package org.tuubes.core

import com.electronwill.collections.RecyclingIndex

import scala.collection.mutable
import scala.reflect.ClassTag

/**
 * A type registry handles the registrations of a given type T, by unique name and by internal id.
 *
 * @tparam T the type registered in this registry
 */
class TypeRegistry[T >: Null <: Type[T]: ClassTag] {
  private[this] val index = new RecyclingIndex[T]
  private[this] val namesMap = new mutable.AnyRefMap[String, T]

  def apply(uniqueName: String): Option[T] = {
    namesMap.get(uniqueName)
  }

  def getOrNull(uniqueName: String): T = {
    namesMap.getOrNull(uniqueName)
  }

  private[tuubes] def apply(internalId: Int): Option[T] = {
    index.apply(internalId)
  }

  private[tuubes] def getOrNull(internalId: Int): T = {
    index.getOrNull(internalId)
  }

  private[tuubes] def register(t: T): Int = {
    namesMap(t.uniqueName) = t
    index += t
  }
}
