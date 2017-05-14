package org.mcphoton;

import java.util.Locale;

/**
 * Interface for types (EntityType, ItemType, etc.)
 *
 * @author TheElectronWill
 */
public interface Type {
	/**
	 * @return the localized name of this type. For example: "Dirt" in english.
	 */
	String getLocalizedName(Locale locale);

	/**
	 * @return the unique and unlocalized name of this type. For example: "minecraft.dirt".
	 */
	String getUniqueName();
}