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

import org.mcphoton.entity.living.LivingEntity;

/**
 * A bat.
 *
 * @see http://minecraft.gamepedia.com/Bat
 * @see http://wiki.vg/Entities#Bat
 * @author DJmaxZPLAY
 * @author TheElectronWill
 */
public interface Bat extends LivingEntity {

	/**
	 * @return true if the bat is hanging.
	 */
	boolean isHanging();

	/**
	 * Sets if the bat is hanging.
	 *
	 * @param hanging true if it's hanging.
	 */
	void setHanging(boolean hanging);

}
