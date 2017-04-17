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
package org.mcphoton.inventory;

import org.mcphoton.entity.living.Player;
import org.mcphoton.item.ItemStack;

/**
 * A player's inventory.
 *
 * @see http://wiki.vg/Inventory
 * @author DJmaxZPLAY
 * @author TheElectronWill
 */
public interface PlayerInventory extends Inventory {

	/**
	 * @return the content of the boots slot.
	 */
	ItemStack getBoots();

	/**
	 * Sets the ItemStack in the boots slot of an entity.
	 */
	void setBoots(ItemStack stack);

	/**
	 * Gets the ItemStack from the leggings slot of an entity.
	 */
	ItemStack getLeggings();

	/**
	 * Sets the ItemStack in the leggings slot of an entity.
	 */
	void setLeggings(ItemStack stack);

	/**
	 * @return the content of the chestplate slot.
	 */
	ItemStack getChestplate();

	/**
	 * Sets the ItemStack in the chestplate slot of an entity.
	 */
	void setChestplate(ItemStack stack);

	/**
	 * @return the content of the helmet slot.
	 */
	ItemStack getHelmet();

	/**
	 * Sets the ItemStack in the helmet slot of an entity.
	 */
	void setHelmet(ItemStack stack);

	/**
	 * @return the ItemStack currently selected by the player, and in its main hand.
	 */
	ItemStack getItemInMainHand();

	/**
	 * Sets the ItemStack in the main hand of an entity.
	 */
	void setItemInMainHand(ItemStack stack);

	/**
	 * @return the content of the offhand slot.
	 */
	ItemStack getItemInOffHand();

	/**
	 * Sets the ItemStack in the off hand of an entity.
	 */
	void setItemInOffHand(ItemStack stack);

	/**
	 * @return the slot number of the currently held stack.
	 */
	int getHeldSlot();

	/**
	 * Sets the slot number of the slot to be held.
	 */
	void setHeldSlot(int heldSlot);

	@Override
	Player getHolder();

}
