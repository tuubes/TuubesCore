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

import java.util.Collection;
import java.util.Iterator;
import org.mcphoton.item.ItemStack;

/**
 * An inventory, which contains some ItemStacks.
 *
 * @author TheElectronWill
 * @author DJmaxZPLAY
 */
public interface Inventory extends Iterable<ItemStack> {

	/**
	 * @return the total number of slots in the inventory.
	 */
	int getSlotsNumber();

	/**
	 * @return a Collection that contains the ItemStacks of the inventory. Any modification to this
	 * collection is reflected in the inventory, and vice-versa.
	 */
	Collection<ItemStack> getContent();

	@Override
	default Iterator<ItemStack> iterator() {
		return getContent().iterator();
	}

	/**
	 * @return the inventory's title.
	 */
	String getTitle();

	/**
	 * Sets the inventory's title.
	 *
	 * @param title the title to set.
	 */
	void setTitle(String title);

	/**
	 * @return the stack at the specified index, or null if there is none.
	 */
	ItemStack getStack(int index);

	/**
	 * Sets the stack at the specified index.
	 *
	 * @return the stack that was at this index, if any, or null.
	 */
	ItemStack setStack(int index, ItemStack stack);

	/**
	 * Adds a stack to this inventory. The ItemStack is placed in the first available slot.
	 *
	 * @return true if the stack was added.
	 */
	boolean addStack(ItemStack stack);

	/**
	 * Removes the stack at the specified index.
	 *
	 * @return the removed stack.
	 */
	ItemStack removeStack(int index);

	/**
	 * @return the inventory's holder (block or entity)
	 */
	InventoryHolder getHolder();

}
