package org.tuubes.core.plugins

import java.util.jar.{JarEntry, JarFile}
import java.{util => ju}

import better.files.File

import scala.util.{Failure, Success, Try}

/**
 * @author TheElectronWill
 */
final class PluginInfos(val name: String, val version: String, val requiredDeps: Seq[String],
						val optionalDeps: Seq[String], val pluginClassName: String,
						val urlClassLoader: OpenURLClassLoader, val file: File) {
	def this(c: PluginInfosCompanion, className: String, cl: OpenURLClassLoader, f: File) = {
		this(c.Name, c.Version, c.RequiredDeps, c.OptionalDeps, className, cl, f)
	}

	override def toString: String =
		s"PluginInfos(" +
		s"name=$name, " +
		s"version=$version, " +
		s"requiredDeps=$requiredDeps, " +
		s"optionalDeps=$optionalDeps, " +
		s"pluginClassName=$pluginClassName, " +
		s"urlClassLoader=$urlClassLoader," +
		s"file=$file)"
}

object PluginInfos {
	def inspect(file: File): Try[PluginInfos] = {
		val url = file.url
		val classLoader = new OpenURLClassLoader(url, classOf[PluginInfos].getClassLoader)
		val pluginClass: Try[Class[_ <: Plugin]] = loadPluginClass(file, classLoader)
		pluginClass.map(extractInfos(_, classLoader, file))
	}

	private def extractInfos(pluginClass: Class[_ <: Plugin], classLoader: OpenURLClassLoader, file: File): PluginInfos = {
		// Gets the plugin's informations from its companion object:
		try {
			val pluginClassName = pluginClass.getCanonicalName
			val companionClass = classLoader.findClass(pluginClassName + "$")
			val companionField = companionClass.getField("$MODULE")
			val companion = companionField.get(null).asInstanceOf[PluginInfosCompanion]
			new PluginInfos(companion, pluginClassName, classLoader, file)
		} catch {
			case e: Exception =>
				throw new PluginLoadingException("Unable to load the PluginInfosCompanion", e)
		}
	}

	private def loadPluginClass(file: File, classLoader: OpenURLClassLoader): Try[Class[_ <: Plugin]] = {
		def isNormalClassName(name: String) = name.indexOf('$') < 0 && name.endsWith(".class")

		def className(entryName: String) = {
			// Removes the trailing ".class" and replaces all '/' by '.'
			entryName.substring(0, entryName.length - 6).replace('/', '.')
		}

		// Gets the first class that inherits from Plugin
		import better.files.CloseableOps
		for (jar: JarFile <- new JarFile(file.toJava).autoClosed) {
			val entries: ju.Enumeration[JarEntry] = jar.entries()
			while (entries.hasMoreElements) {
				val entry = entries.nextElement()
				val entryName = entry.getName
				if (isNormalClassName(entryName)) {
					val clazz = classLoader.findClass(className(entryName))
					if (classOf[Plugin].isAssignableFrom(clazz)) {
						return Success(clazz.asInstanceOf[Class[_ <: Plugin]])
					}
				}
			}
		}
		Failure(new PluginLoadingException("No plugin class found"))
	}
}