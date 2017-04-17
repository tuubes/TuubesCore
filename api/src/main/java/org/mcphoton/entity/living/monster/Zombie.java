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
package org.mcphoton.entity.living.monster;

import org.mcphoton.entity.living.InsentientEntity;

/**
 * A zombie.
 *
 * @see http://minecraft.gamepedia.com/Zombie
 * @see http://minecraft.gamepedia.com/Zombie#Data_values
 * @see http://wiki.vg/Entities#Zombie
 * @author TheElectronWill
 */
public interface Zombie extends InsentientEntity {

	/**
	 * @return true if the zombie is a baby.
	 */
	boolean isBaby();

	/**
	 * Sets true if the zombie is a baby.
	 *
	 * @param baby true if the zombie is a baby.
	 */
	void setBaby(boolean baby);

	/**
	 * @return the zombie's type. 0: original zombie, 1: zombie farmer villager, 2: zombie librarian villager,
	 * 3: zombie priest villager, 4: zombie blacksmith villager, 5: zombie butcher villager, 6: husk
	 */
	int getZombieType();

	/**
	 * Sets the zombie's type.
	 *
	 * @param type the zombie's type to set. 0: original zombie, 1: zombie farmer villager, 2: zombie
	 * librarian villager, 3: zombie priest villager, 4: zombie blacksmith villager, 5: zombie butcher
	 * villager, 6: husk
	 */
	void setZombieType(int type);

}
