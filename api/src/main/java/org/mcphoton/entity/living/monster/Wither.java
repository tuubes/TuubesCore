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
 * A wither.
 *
 * @see http://minecraft.gamepedia.com/Wither
 * @see http://wiki.vg/Entities#Wither
 * @author TheElectronWill
 */
public interface Wither extends InsentientEntity {

	/**
	 * @return the number of ticks while the wither is invulnerable.
	 */
	int getInvulnerableTicks();

	/**
	 * Sets the number of ticks while the wither is invulnerable.
	 *
	 * @param ticks the number of ticks while the wither is invulnerable.
	 */
	void setInvulnerableTicks(int ticks);
}
