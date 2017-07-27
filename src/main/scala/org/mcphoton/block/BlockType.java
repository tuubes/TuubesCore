package org.mcphoton.block;

import org.mcphoton.utils.Type;

/**
 * @author TheElectronWill
 */
public interface BlockType extends Type {
	/**
	 * Checks if this type is a variant of another type.
	 * <p>
	 * This method defines an equivalence relation, similar to {@link Object#equals(Object)}. It has
	 * the following properties:
	 * <li>Symmetry: t.isVariant(t) return true</li>
	 * <li>Transitivity: a.isVariant(b) and b.isVariant(a) return the same value</li>
	 * <li>Transitivity: Iff a.isVariant(b) and b.isVariant(c) are true, then a.isVariant(c) is
	 * also true.</li>
	 *
	 * @param other the type to compare to
	 * @return true if this type is a variant of the specified type
	 */
	boolean isVariant(BlockType other);
}