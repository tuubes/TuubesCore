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

/**
 * A wolf.
 *
 * http://wiki.vg/Entities#Wolf
 *
 * @author DJmaxZPLAY
 * @author TheElectronWill
 */
public interface Wolf extends TameableEntity {

	/**
	 * @return true if the wolf is angry.
	 */
	boolean isAngry();

	/**
	 * Sets if the wolf is angry.
	 *
	 * @param angry true if it's angry.
	 */
	void setAngry(boolean angry);

	/**
	 * @return the color of the wolf's collar.
	 */
	int getCollarColor();

	/**
	 * Sets the color of the wolf's collar.
	 *
	 * @param color the color of the wolf's collar.
	 */
	void setCollarColor(int color);

}
