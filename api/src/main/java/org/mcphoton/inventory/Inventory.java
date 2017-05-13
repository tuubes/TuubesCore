package org.mcphoton.inventory;

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
	 * @return the removed stack, or null if there was no stack at this index.
	 */
	ItemStack removeStack(int index);

	/**
	 * @return the inventory's holder (block or entity)
	 */
	InventoryHolder getHolder();
}