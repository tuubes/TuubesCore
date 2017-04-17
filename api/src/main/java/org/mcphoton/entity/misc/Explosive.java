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
package org.mcphoton.entity.misc;

/**
 * An explosive entity.
 *
 * @author DJmaxZPLAY
 * @author TheElectronWill
 */
public interface Explosive {

	/**
	 * @return the explosion's radius, in blocks.
	 */
	int getExplosionRadius();

	/**
	 * Sets the explosion's radius.
	 *
	 * @param radius the radius in blocks.
	 */
	void setExplosionRadius(int radius);

	/**
	 * @return true if this explosion will create fire, false if it won't.
	 */
	boolean isIncendiary();

	/**
	 * Sets whether the explosion will create fire or not.
	 *
	 * @param incendiary true to make the explosion create fire.
	 */
	void setIncendiary(boolean incendiary);

}
