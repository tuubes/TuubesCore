package org.mcphoton.plugin

/**
 * A ClassLoader that delegates `findClass` to an OpenURLClassLoader. This allows to use an
 * OpenURLClassLoader with a parent ClassLoader different from its normal parent.
 *
 * @author TheElectronWill
 */
final class DelegateClassLoader(val delegate: OpenURLClassLoader, val parent: ClassLoader) extends
	ClassLoader(parent) {
	override def findClass(name: String): Class[_] = delegate.findClass(name)

	override def toString = s"DelegateCL($delegate) <- $parent"
}