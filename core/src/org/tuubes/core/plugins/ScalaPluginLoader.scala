package org.tuubes.core.plugins

import better.files.{File, Files}

import scala.collection.mutable
import scala.util.control.NonFatal
import scala.util.{Failure, Success}

import org.tuubes.core.TuubesServer.logger

/**
 * @author TheElectronWill
 */
final class ScalaPluginLoader extends PluginLoader {
  private val loaded = new mutable.AnyRefMap[String, LoadedNode]()
  private var unloadOrder: mutable.Buffer[LoadedNode] = mutable.Buffer.empty

  override def plugins: Iterable[Plugin] = loaded.values.map(_.plugin)

  override def plugin(name: String): Option[Plugin] =
    loaded.get(name).map(_.plugin)

  override def load(files: Files): Int = {
    def parentFailed(dependent: Node, parent: String, errors: mutable.ArrayBuffer[String]): Unit = {
      dependent.markInvalid()
      val name = dependent.data.name
      errors += s"Unable to load '$name' because '$parent' failed to load."
      dependent.hardDependents.foreach(parentFailed(_, name, errors))
    }

    val errors = new mutable.ArrayBuffer[String]
    val graph = new DependencyGraph(errors)
    // Inspect the files and detect plugin infos
    var fileCount = 0
    for (file: File <- files) {
      logger.debug(s"Inspecting plugin file $file")
      fileCount += 1
      PluginInfos.inspect(file) match {
        case Success(infos) =>
          logger.debug("Inspection successfull")
          graph.register(infos)
        case Failure(error) =>
          logger.debug("Inspection failed!")
          logger.error(s"Invalid plugin file $file", error)
          errors += ""
      }
    }
    if (errors.length == fileCount) {
      logger.error(s"Found ${errors.length} error(s), no plugin could be loaded.")
      0 // All the inspections have failed
    } else {
      this.synchronized {
        // Registers the currently loaded plugins, so they can be used as dependencies by the new plugins
        logger.debug("Registering plugin data to the dependency graph")
        loaded.values.map(_.infos).foreach(graph.register)

        // Dependency resolution: find the optimal loading order and detect potential errors
        logger.debug("Building and resolving the graph")
        graph.build()
        val resolved = graph.resolve().resolvedItems

        // Loading: create an instance of each plugin, in the right order
        logger.debug("Creating an instance of each plugin")
        val loadOrder = new mutable.ArrayBuffer[LoadedNode](resolved.size)
        for (r <- resolved; node = r.node if r.node.isValid) {
          val infos = node.data
          val name = infos.name
          try {
            val pluginClass = r.fullLoader.loadClass(infos.pluginClassName)
            val instance = pluginClass.asInstanceOf[Class[Plugin]].newInstance()
            val loadedNode = LoadedNode(instance, infos)
            loaded(name) = loadedNode
            loadOrder += loadedNode
          } catch {
            // The plugin failed to load, therefore all its hard dependents fail.
            case NonFatal(e) =>
              e.printStackTrace()
              logger.error(s"Cannot create an instance of '$name:${infos.version}'", e)
              node.hardDependents.foreach(parentFailed(_, name, errors))
          }
        }

        // TODO reload previously loaded plugins if new optional dependencies have been added

        // Call onLoad() in the right order
        for (node <- loadOrder) {
          val plugin = node.plugin
          if (plugin.state == PluginState.LOADED) {
            logger.debug(s"Calling onLoad() of ${node.infos.name}")
            plugin.onLoad()
            plugin.state = PluginState.ENABLED
          }
        }

        // TODO enable the plugins in their worlds */
        val el = errors.length
        val ll = loadOrder.length
        if (el != 0) {
          logger.error(s"Found $el dependency error(s), only $el out of ${el + ll} plugins could be loaded")
          errors.foreach(logger.error)
        }

        // Updates the unloadOrder
        logger.debug("Storing data for the ScalaPluginLoader")
        unloadOrder = loadOrder.reverse

        // Returns the number of loaded plugins
        loadOrder.length
      }
    }
  }

  override def unload(p: Plugin): Unit = {
    if (p.state == PluginState.ENABLED) {
      // Unloads hard deps and reloads soft deps
      this.synchronized {
        // TODO disable the plugin in its worlds
        val node = loaded.remove(p.name).get // Non-null because the plugin is loaded and therefore in the map
        val infos = node.infos
        infos.requiredDeps.map(loaded(_).plugin).foreach(unload)
        infos.optionalDeps.flatMap(loaded.get).map(_.plugin).foreach(reload)
        notifyUnload(p)
        unloadOrder -= node
      }
    }
  }

  override def unloadAll(): Unit = {
    this.synchronized {
      for (loadedNode <- unloadOrder) {
        val plugin = loadedNode.plugin
        // TODO disable the plugin in its worlds
        notifyUnload(plugin)
      }
      unloadOrder = mutable.Buffer.empty
    }
  }

  private def notifyUnload(p: Plugin): Unit = {
    p.onUnload()
    p.state = PluginState.DISABLED
  }

  /** Reloads a plugin from its file */
  private def reload(p: Plugin): Unit = {
    val file = loaded(p.name).infos.file
    unload(p)
    load(file)
  }

  private final case class LoadedNode(plugin: Plugin, infos: PluginInfos) {}
}
