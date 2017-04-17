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
package org.mcphoton.entity.living.animal;

import org.mcphoton.entity.vehicle.Vehicle;
import org.mcphoton.inventory.HorseInventory;
import org.mcphoton.inventory.InventoryHolder;

/**
 * A horse.
 *
 * @see http://minecraft.gamepedia.com/Horse
 * @see http://minecraft.gamepedia.com/Horse#Data_values
 * @author DJmaxZPLAY
 * @author TheElectronWill
 */
public interface Horse extends TameableEntity, Vehicle, InventoryHolder {

	/**
	 * @return true if the horse is carrying a chest.
	 */
	boolean isCarryingChest();

	/**
	 * Sets if the horse is carrying a chest.
	 *
	 * @param chest true if the horse is carrying a chest.
	 */
	void setCarryingChest(boolean chest);

	/**
	 * @return the horse's taming level, between 0 and 100.
	 */
	int getTamingLevel();

	/**
	 * Sets the horse's taming level.
	 *
	 * @param level the level to set, between 0 and 100.
	 */
	void setTamingLevel(int level);

	/**
	 * @return the height, in blocks, that this horse can jump to.
	 */
	double getJumpHeight();

	/**
	 * Sets the height that this horse can jump to.
	 *
	 * @param strength the height in blocks.
	 */
	void setJumpHeight(double height);

	@Override
	HorseInventory getInventory();

	Variant getVariant();

	void setVariant(Variant variant);

	Color getColor();

	void setColor(Color color);

	Style getStyle();

	void setStyle(Style style);

	enum Variant {
		HORSE,
		DONKEY,
		MULE,
		ZOMBIFIED,
		SKELETON;
	}

	enum Color {
		WHITE,
		CREAMY,
		CHESTNUT,
		BROWN,
		BLACK,
		GRAY,
		DARK_BROWN;
	}

	enum Style {
		NONE,
		WHITE,
		WHITEFIELD,
		WHITE_DOTS,
		BLACK_DOTS;
	}

}
