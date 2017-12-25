package org.tuubes.plugin

import java.util.concurrent.ConcurrentHashMap

import org.tuubes.world.World

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
 * @author TheElectronWill
 */
trait PluginSystem[P <: Plugin] {
	def enable(p: P)

	def disable(p: P)

	def get(name: String): Option[P] = plugins.get(name)

	protected[this] val plugins: mutable.Map[String, P] = new ConcurrentHashMap[String, P].asScala

	def iterate: Iterator[P] = plugins.valuesIterator
}

object PluginSystem {
	/**
	 * Gets the world's PluginSystem. The advantage of this method over `world.pluginSystem` is
	 * that here the world is passed implicitely.
	 *
	 * @return the world's PluginSystem
	 */
	def apply(implicit w: World): PluginSystem[Plugin] = w.pluginSystem

	/**
	 *
	 * @return the global plugin system
	 */
	def global: PluginSystem[GlobalPlugin] = GlobalPluginSystem
}