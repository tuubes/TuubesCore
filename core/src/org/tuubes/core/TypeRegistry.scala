package org.tuubes.core

import better.files.File
import com.electronwill.collection.ArrayMap
import com.electronwill.util.StringUtils

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

  /**
   * Searches a type by name. Throws an exception if not found.
   *
   * @param uniqueName the type's name
   * @return the type that has the given name
   */
  def apply(uniqueName: String): T = namesMap(uniqueName)

  /**
   * Searches a type by name. Returns `None` if not found.
   *
   * @param uniqueName the type's name
   * @return the type that has the given name, or None
   */
  def get(uniqueName: String): Option[T] = namesMap.get(uniqueName)

  /**
   * Searches a type by id. Throws an exception if not found.
   *
   * @param internalId the type's id
   * @return the type that has the given id
   */
  private[tuubes] def apply(internalId: Int): T = {
    val typeOrNull = index(internalId)
    if (typeOrNull == null) throw new NoSuchElementException(s"No type with id $internalId")
    typeOrNull
  }

  /**
   * Searches a type by id. Returns `None` if not found.
   *
   * @param internalId the type's id
   * @return the type that has the given id, or None
   */
  private[tuubes] def get(internalId: Int): Option[T] = index.get(internalId)

  /**
   * Registers a type and returns its internal id.
   *
   * @param t the type to register
   * @return its internal id
   */
  private[tuubes] def register(t: T): Int = {
    namesMap(t.uniqueName) = t
    val key = preRegistrations.getOrElse(t.uniqueName, index.size)
    index(key) = t
    key
  }

  /**
   * Imposes the id of a type.
   *
   * @param uniqueName the type's name
   * @param id the type's id
   */
  private[tuubes] def preRegister(uniqueName: String, id: Int): Unit = {
    preRegistrations(uniqueName) = id
  }

  /**
   * Deletes the information about the pre-registrations.
   * This also prevents new types to be registered.
   */
  private[tuubes] def ditchPreRegistrations(): Unit = {
    preRegistrations = null
  }

  /**
   * Saves the names and ids of the types.
   *
   * @param file the file to write to
   */
  private[tuubes] def saveTo(file: File): Unit = {
    for (writer <- file.bufferedWriter) {
      for ((key, typ) <- index) {
        writer.write(key.toString)
        writer.write('=')
        writer.write(typ.uniqueName)
        writer.newLine()
      }
    }
  }

  /**
   * Pre-registers the types with the information contained in a file.
   *
   * @param file the file to read
   */
  private[tuubes] def readFrom(file: File): Unit = {
    for (line <- file.lineIterator) { //the iterator is auto-closed at the end of the iteration
      val parts = StringUtils.split(line, '=')
      val key = parts.get(0).toInt
      val name = parts.get(1)
      preRegister(name, key)
    }
  }
}
