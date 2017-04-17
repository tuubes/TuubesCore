/*
 * Copyright (c) 2016 MCPhoton <http://mcphoton.org> and contributors.
 *
 * This file is part of the Photon API <https://github.com/mcphoton/Photon-API>.
 *
 * The Photon API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Photon API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mcphoton.utils;

/**
 * Interface for types (EntityType, ItemType, etc.)
 *
 * @author TheElectronWill
 */
public interface Type {

	/**
	 * @return the localized name of this type. For example: "Dirt".
	 */
	String getLocalizedName();

	/**
	 * @return the unique and unlocalized name of this type. For example: "minecraft.dirt".
	 */
	String getUniqueName();

	/**
	 * @return the unique id of this type.
	 */
	int getId();

}
