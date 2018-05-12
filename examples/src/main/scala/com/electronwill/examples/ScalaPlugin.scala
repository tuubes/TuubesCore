package com.electronwill.examples

import org.tuubes.core.LocalWorld
import org.tuubes.core.plugins.{Plugin, PluginDescription}

/**
 * An example of plugin class.
 * Note: the override modifiers aren't mandatory here, but I've let them to show that the methods
 * and values come from the parent trait.
 */
class ScalaPlugin extends Plugin {
  override val description = ScalaPlugin

  override def onLoad() = {
    logger.info("Plugin loaded!")
  }

  override def onUnload() = {
    logger.info("Plugin unloaded!")
  }

  override def onEnable(world: LocalWorld) = {
    logger.info(s"Plugin enabled in world $world")
  }

  override def onDisable(world: LocalWorld) = {
    logger.info(s"Plugin disabled in world $world")
  }
}

/**
 * Companion object that extends PluginDescription. This is necessary for Tuubes to load the plugin.
 */
object ScalaPlugin extends PluginDescription {
  override val name = "ElectronWill's Example"
  override val version = "1.0.0"
  override val optionalDeps = Seq("OtherPluginA", "OtherPluginB")
  override val requiredDeps = Nil
}
