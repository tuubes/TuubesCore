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
 * Azombie pigman.
 *
 * @see http://minecraft.gamepedia.com/Zombie_Pigman
 * @author TheElectronWill
 */
public interface ZombiePigMan extends InsentientEntity {

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
	 * @return the anger level of the zombie pigman, ie the number of ticks until it becomes neutral. Less or
	 * equal to 0 = neutral; bigger than 0 = angry. This value decreases every tick if its greater than 0.
	 */
	int getAnger();

	/**
	 * Sets the anger level of the zombie pigman.
	 *
	 * @param anger the number of ticks until it becomes neutral. Less or equal to 0 = neutral; bigger than 0
	 * = angry. This value decreases every tick if its greater than 0.
	 */
	void setAnger(int anger);

}
