package org.mcphoton.plugin

import java.util.concurrent.CopyOnWriteArrayList

import org.mcphoton.world.World

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
 * A GlobalPlugin is a plugin that may be enabled in several worlds, or even in no world.
 * <p>
 * Let G be an implementation of GlobalPlugin. There may be only one instance of G for the entire
 * server. This instance will report all the worlds (possibly none) in which it is enabled.
 *
 * @author TheElectronWill
 */
abstract class GlobalPlugin extends Plugin {
	private[plugin] final val _worlds: mutable.Buffer[World] = new CopyOnWriteArrayList[World]().asScala

	override def worlds: Iterable[World] = _worlds

	/**
	 * Called when the plugin is "globally" enabled, ie enabled for the server but not yet for
	 * specific worlds.
	 */
	def onGlobalEnable()

	/**
	 * Called when the plugin is "globally" disabled, ie completely disabled from the server. This
	 * method is called after the onDisable(world) is called for each world.
	 */
	def onGlobalDisable()

	/**
	 * Called when the plugin is enabled in a world.
	 *
	 * @param w the world
	 */
	def onEnable(implicit w: World)

	/**
	 * Called when the plugin is disabled in a world
	 *
	 * @param w the world
	 */
	def onDisable(implicit w: World)
}