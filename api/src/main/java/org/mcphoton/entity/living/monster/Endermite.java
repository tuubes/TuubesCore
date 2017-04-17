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
 * An endermite.
 *
 * @see http://minecraft.gamepedia.com/Endermite
 * @author TheElectronWill
 */
public interface Endermite extends InsentientEntity {

	/**
	 * @return the number of ticks the endermite has been living for.
	 */
	int getLifeTicks();

	/**
	 * Sets the number of ticks the endermite has been flying for.
	 *
	 * @param ticks the number of ticks the endermite has been flying for.
	 */
	void setLifeTicks(int ticks);

	/**
	 * @return the number of ticks before the endermite dies (it dies when lifeTicks == lifeTime).
	 */
	int getLifeTime();

	/**
	 * Sets the rocket's lifetime.
	 *
	 * @param ticks the number of ticks before the endermite dies (it dies when lifeTicks == lifeTime).
	 */
	void setLifeTime(int ticks);
}
