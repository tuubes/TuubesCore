package org.mcphoton.item;

import org.mcphoton.entity.mobs.Player;

/**
 * A player's inventory.
 *
 * @author DJmaxZPLAY
 * @author TheElectronWill
 * @see <a href="http://wiki.vg/Inventory">wiki.vg - Inventory</a>
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
	ItemStack getMainHand();

	/**
	 * Sets the ItemStack in the main hand of an entity.
	 */
	void setMainHand(ItemStack stack);

	/**
	 * @return the slot number of the currently held stack.
	 */
	int getHeldSlot();

	/**
	 * Sets the slot number of the slot to be held.
	 */
	void setHeldSlot(int heldSlot);

	/**
	 * @return the content of the offhand slot.
	 */
	ItemStack getOffHand();

	/**
	 * Sets the ItemStack in the off hand of an entity.
	 */
	void setOffHand(ItemStack stack);

	@Override
	Player getHolder();
}