package org.tuubes.core.plugins

import java.util.concurrent.ConcurrentHashMap

import org.tuubes.core.engine.World

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
 * @author TheElectronWill
 */
trait PluginManager[P <: Plugin] extends Iterable[P] {
	def enable(p: P): Unit

	def disable(p: P): Unit

	def get(name: String): Option[P] = plugins.get(name)

	protected[this] val plugins: mutable.Map[String, P] = new ConcurrentHashMap[String, P].asScala

	override def iterator: Iterator[P] = plugins.valuesIterator
}

object PluginManager {
	/**
	 * @return the PluginManager for the given world
	 */
	def apply(w: World): PluginManager[Plugin] = w.pluginManager

	/**
	 * @return the global plugin system
	 */
	def global: PluginManager[Plugin] = GlobalPluginSystem
}