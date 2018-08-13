package org.tuubes.core

/**
 * A Type of *something*, identitified by its name and internal ID (the latter is only accessible
 * from Tuubes' internals).
 *
 * @author TheElectronWill
 */
abstract class Type[T >: Null <: Type[T]](val uniqueName: String, reg: TypeRegistry[T]) {
  /**
	 * Checks if this type is the same as or a variant of some other type.
	 * <p>
	 * This method defines an equivalence relation, similar to [[Object#equals(Object)]]. It has
	 * the following properties:
	 * <li>Reflexivity: `t.isVariantOf(t)` returns true for all t</li>
	 * <li>Symmetry: `a.isVariantOf(b)` and `b.isVariantOf(a)` return the same value</li>
	 * <li>Transitivity: `a.isVariantOf(b)` and `b.isVariantOf(c)` are true if and only if
   * `a.isVariantOf(c)` is true.</li>
	 *
	 * @param t the type to compare to this type
	 * @return true if this type is a variant of the specified type
	 */
  def isVariantOf(t: T): Boolean = t eq this

  /**
	 * The type's unique id, internal to Tuubes.
	 *
	 * @return the type's internal unique id.
	 */
  private[tuubes] val internalId: Int = reg.register(this.asInstanceOf[T])
}
