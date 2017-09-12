package org.mcphoton

/**
 * @author TheElectronWill
 */
abstract class Type[T <: Type[T]] protected(val uniqueName: String) {
	/**
	 * Checks if this type is the same as or a variant of some other type.
	 * <p>
	 * This method defines an equivalence relation, similar to  [[Object#equals(Object)]]. It has
	 * the following properties:
	 * <li>Symmetry: t.isVariant(t) returns true for all t</li>
	 * <li>Transitivity: a.isVariant(b) and b.isVariant(a) return the same value</li>
	 * <li>Transitivity: a.isVariant(b) and b.isVariant(c) are true if and only if a.isVariant(c)
	 * is true.</li>
	 *
	 * @param t the type to compare to this type
	 * @return true if this type is a variant of the specified type
	 */
	def isVariant(t: T): Boolean = t.id == id

	/**
	 * The type's unique id. This value depends on the MC version and is not part of the
	 * plugin API. Therefore it is only accessible inside the mcphoton package.
	 *
	 * @return the type's unique id.
	 */
	private[mcphoton] val id: Int
}