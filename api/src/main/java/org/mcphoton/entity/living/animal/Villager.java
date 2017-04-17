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

import org.mcphoton.entity.living.AgeableEntity;
import org.mcphoton.entity.living.Player;
import org.mcphoton.inventory.InventoryHolder;

// TODO: 25/07/2016 Add trade 
public interface Villager extends AgeableEntity, InventoryHolder {

	/**
	 * Gets the current profession of this villager.
	 *
	 * @return {@link Profession}
	 */
	Profession getProfession();

	/**
	 * Set the current profession of this villager.
	 */
	void setProfession(Profession profession);

	/**
	 * Gets the player this villager is trading with, or null if it is not currently trading.
	 *
	 * @return {@link Player}
	 */
	Player getTrader();

	/**
	* Gets whether this villager is currently trading.
	*/
	boolean isTrading();

	/**
	 * Defines a profession of {@link Villager}.
	 *
	 * @author Vinetos
	 */
	enum Profession {
		/**
		 * Red color
		 */
		BLACKSMITH,
		/**
		 * Butcher profession.
		 */
		BUTCHER,
		/**
		 * Farmer profession.
		 */
		FARMER,
		/**
		 * Husk
		 */
		HUSK,
		/**
		 * Librarian profession.
		 */
		LIBRARIAN,
		/**
		 * Normal villager.
		 */
		NORMAL,
		/**
		 * Priest profession.
		 */
		PRIEST
	}


}
