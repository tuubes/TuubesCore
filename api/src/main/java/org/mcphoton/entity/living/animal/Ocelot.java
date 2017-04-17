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
 * An ocelot (also known as "cat").
 *
 * @see http://wiki.vg/Entities#Ocelot
 * @author DJmaxZPLAY
 * @author TheElectronWill
 */
public interface Ocelot extends TameableEntity {

	/**
	 * An ocelot's type.
	 */
	int TYPE_WILD = 0, TYPE_BLACK = 1, TYPE_RED = 2, TYPE_SIAMESE = 3;

	/**
	 * @return the ocelot's type. 0:Wild, 1:Black, 2:Red, 3:Siamese.
	 */
	int getOcelotType();

	/**
	 * Set the ocelot's type.
	 *
	 * @param type the type to set. 0:Wild, 1:Black, 2:Red, 3:Siamese.
	 */
	void setOcelotType(int type);
	
}
