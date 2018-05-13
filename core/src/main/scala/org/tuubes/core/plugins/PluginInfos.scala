package org.tuubes.core.plugins

import java.lang.reflect.Modifier
import java.util.jar.{JarEntry, JarFile}
import java.{util => ju}

import better.files.File
import org.tuubes.core.TuubesServer
import org.tuubes.core.TuubesServer.logger

import scala.util.{Failure, Success, Try}

/**
 * @author TheElectronWill
 */
final class PluginInfos(val name: String,
                        val version: String,
                        val requiredDeps: Seq[String],
                        val optionalDeps: Seq[String],
                        val pluginClassName: String,
                        val urlClassLoader: OpenURLClassLoader,
                        val file: File) {
  def this(c: PluginDescription, className: String, cl: OpenURLClassLoader, f: File) = {
    this(c.name, c.version, c.requiredDeps, c.optionalDeps, className, cl, f)
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

  private def extractInfos(pluginClass: Class[_ <: Plugin],
                           classLoader: OpenURLClassLoader,
                           file: File): PluginInfos = {
    // Gets the plugin's informations from its companion object:
    try {
      val pluginClassName = pluginClass.getCanonicalName
      logger.debug(s"Plugin class found: $pluginClassName")
      val companionClass = classLoader.findClass(pluginClassName + "$")
      val companionField = companionClass.getField("MODULE$")
      val companion = companionField.get(null).asInstanceOf[PluginDescription]
      new PluginInfos(companion, pluginClassName, classLoader, file)
    } catch {
      case e: Exception =>
        throw new PluginLoadingException(
          "Unable to load the plugin description, please check that the plugin's main" +
            "class has a companion object extending PluginDescription", e)
    }
  }

  private def loadPluginClass(file: File,
                              classLoader: OpenURLClassLoader): Try[Class[_ <: Plugin]] = {
    def isNormalClassName(name: String) = {
      name.indexOf('$') < 0 && name.endsWith(".class")
    }

    def className(entryName: String) = {
      // Removes the trailing ".class" and replaces all '/' by '.'
      entryName.substring(0, entryName.length - 6).replace('/', '.')
    }

    def isConcrete(c: Class[_]): Boolean = {
      // Checks if the class is neither abstract nor an interface
      val modifiers = c.getModifiers
      !Modifier.isAbstract(modifiers) && !Modifier.isInterface(modifiers)
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
          if (classOf[Plugin].isAssignableFrom(clazz) && isConcrete(clazz)) {
            return Success(clazz.asInstanceOf[Class[_ <: Plugin]])
          }
        }
      }
    }
    val msg =
      "No plugin class found, please check that the jar file contains one class that extends Plugin"
    Failure(new PluginLoadingException(msg))
  }
}
