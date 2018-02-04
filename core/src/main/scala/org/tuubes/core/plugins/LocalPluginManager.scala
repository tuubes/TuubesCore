package org.tuubes.core.plugins

import better.files.{File, Files}

import scala.collection.mutable.{AnyRefMap, ArrayBuffer}
import scala.util.{Failure, Success}

/**
 * @author TheElectronWill
 */
final class LocalPluginManager extends PluginManager[Plugin] {
	private val nodes = new AnyRefMap[String, LoadedNode]
	private var loadOrder: Array[LoadedNode] = _

	//TODO possibility to load more plugins during the game

	def initialLoad(files: Files): Unit = {
		this.synchronized {// TODO optimize this
			// Inspection
			val errors = new ArrayBuffer[String]
			val graph = new DependencyGraph(errors)
			for (file: File <- files) {
				PluginInfos.inspect(file) match {
					case Success(infos) => graph.register(infos)
					case Failure(error) => errors += s"Invalid plugin file '$file' - $error"
				}
			}
			// Dependency resolution
			graph.build()
			val solution = graph.resolve()
			val resolved = solution.resolvedItems

			// Loading
			val loadBuffer = new ArrayBuffer[LoadedNode](resolved.size)
			for (r <- resolved; node = r.node if r.node.isValid) {
				val infos = node.data
				val name = infos.name
				try {
					val pluginClass = r.fullLoader.loadClass(infos.pluginClassName)
					val instance = pluginClass.asInstanceOf[Class[Plugin]].newInstance()
					val loadedNode = new LoadedNode(instance, infos)
					nodes(name) = loadedNode
					loadBuffer += loadedNode
				} catch {
					// The plugin failed to load, therefore all its hard dependents fail.
					case error =>
						error.printStackTrace()
						errors += s"Cannot create an instance of '$name:${infos.version}' - $error"
						node.hardDependents.foreach(parentFailed(_, name, errors))
				}
			}
			loadOrder = loadBuffer.toArray[LoadedNode]
		}
	}

	private def parentFailed(dependent: Node, parent: String, errors: ArrayBuffer[String]): Unit = {
		dependent.markInvalid()
		val name = dependent.data.name
		errors += s"Unable to load '$name' because '$parent' failed to load."
		dependent.hardDependents.foreach(parentFailed(_, name, errors))
	}

	//TODO these two:
	override def enable(p: Plugin): Unit = ???
	override def disable(p: Plugin): Unit = ???

	private final case class LoadedNode(p: Plugin,
										i: PluginInfos,
										var dependents: Array[LoadedNode]) {
		def this(p: Plugin, i: PluginInfos) = {
			this(p, i, null)
		}

		def findDependents(node: Node): Unit = {
			val resolvedDependents = node.hardDependents ++ node.softDependents
			val buffer = new ArrayBuffer[LoadedNode](resolvedDependents.size)
			for (dependent <- resolvedDependents) {
				val loaded = nodes(dependent.data.name)
				buffer += loaded
			}
			dependents = buffer.toArray[LoadedNode]
		}
	}
}