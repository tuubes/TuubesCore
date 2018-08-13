package org.tuubes.core

import better.files.File
import com.electronwill.collections.ArrayMap
import com.electronwill.utils.StringUtils

import scala.collection.mutable
import scala.reflect.ClassTag

/**
 * A type registry handles the registrations of a given type T, by unique name and by internal id.
 *
 * @tparam T the type registered in this registry
 */
class TypeRegistry[T >: Null <: Type[T] : ClassTag] {
  private[this] val index = new ArrayMap[T](64, null)
  private[this] val namesMap = new mutable.AnyRefMap[String, T](64)
  private[this] var preRegistrations = new mutable.AnyRefMap[String, Int](64)

  def get(uniqueName: String): Option[T] = {
    namesMap.get(uniqueName)
  }

  def getOrNull(uniqueName: String): T = {
    namesMap.getOrNull(uniqueName)
  }

  private[tuubes] def get(internalId: Int): Option[T] = {
    index.get(internalId)
  }

  private[tuubes] def getOrNull(internalId: Int): T = {
    index(internalId)
  }

  private[tuubes] def register(t: T): Int = {
    namesMap(t.uniqueName) = t
    val key = preRegistrations.getOrElse(t.uniqueName, index.size)
    index(key) = t
    key
  }

  private[tuubes] def preRegister(uniqueName: String, id: Int): Unit = {
    preRegistrations(uniqueName) = id
  }

  private[tuubes] def ditchPreRegistrations(): Unit = {
    preRegistrations = null
  }
}
