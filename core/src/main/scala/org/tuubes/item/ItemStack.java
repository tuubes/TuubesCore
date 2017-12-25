package org.tuubes.item;

import java.util.Objects;

/**
 * A stack of items.
 *
 * @author TheElectronWill
 */
public class ItemStack {
	protected ItemType type;
	protected int maxSize, size, damage;

	public ItemStack(ItemType type, int maxSize, int size, int damage) {
		this.type = type;
		this.maxSize = maxSize;
		this.size = size;
		this.damage = damage;
	}

	public ItemStack(ItemType type) {
		this(type, 64, 0, 0);
	}

	/**
	 * @return true if the stack is empty.
	 */
	boolean isEmpty() {
		return size == 0;
	}

	/**
	 * @return true if the stack is full, ie if its size is equal to its max size.
	 */
	boolean isFull() {
		return size == maxSize;
	}

	/**
	 * @return the stack's size.
	 */
	int getSize() {
		return size;
	}

	/**
	 * Adds the given value to the current size value.
	 *
	 * @param delta the value to add
	 */
	void addSize(int delta) {
		size += delta;
	}

	/**
	 * Sets the stack's size.
	 */
	void setSize(int size) {
		this.size = size;
	}

	/**
	 * @return the max stack's size.
	 */
	int getMaxSize() {
		return maxSize;
	}

	/**
	 * @return the item's type.
	 */
	ItemType getType() {
		return type;
	}

	/**
	 * @return the stack's damage.
	 */
	int getDamage() {
		return damage;
	}

	/**
	 * Adds the given value to the current damage value.
	 *
	 * @param delta the value to add
	 */
	void addDamage(int delta) {
		this.damage += delta;
	}

	/**
	 * Sets the stack's damage.
	 */
	void setDamage(int damage) {
		this.damage = damage;
	}

	@Override
	public String toString() {
		return "ItemStack{"
			   + "type="
			   + type
			   + ", maxSize="
			   + maxSize
			   + ", size="
			   + size
			   + ", damage="
			   + damage
			   + '}';
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 47 * hash + Objects.hashCode(this.type);
		hash = 47 * hash + this.maxSize;
		hash = 47 * hash + this.size;
		hash = 47 * hash + this.damage;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof ItemStack) {
			ItemStack other = (ItemStack)obj;
			return size == other.size && type.equals(other.type) && maxSize == other.maxSize;
		}
		return false;
	}
}