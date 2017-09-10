package org.mcphoton.plugin

import java.io.File
import java.net.{URL, URLClassLoader}

/**
 * An URLClassLoader with a public method findClass.
 *
 * @author TheElectronWill
 */
final class OpenURLClassLoader(url: URL, parent: ClassLoader) extends URLClassLoader(Array(url), parent) {
	// make public
	override def findClass(name: String): Class[_] = super.findClass(name)

	override def toString = s"OpenUrlCL(${new File(url.getFile).getName})"
}