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
 * A skeleton.
 *
 * @see http://wiki.vg/Entities#Skeleton
 * @see http://minecraft.gamepedia.com/Skeleton
 * @author DJmaxZPLAY
 * @author TheElectronWill
 */
public interface Skeleton extends InsentientEntity {

	int TYPE_NORMAL = 0, TYPE_WITHER = 1, TYPE_STRAY = 2;

	/**
	 * @return the skeleton's type.
	 */
	int getSkeletonType();

	/**
	 * Sets the skeleton's type.
	 *
	 * @param type the skeleton's type.
	 */
	void setSkeletonType(int type);
}
