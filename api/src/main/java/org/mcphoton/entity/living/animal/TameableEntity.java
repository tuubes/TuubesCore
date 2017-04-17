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

import java.util.Optional;
import org.mcphoton.entity.Entity;

/**
 * An entity that can be tamed.
 *
 * @author TheElectronWill
 */
public interface TameableEntity extends Entity {

	/**
	 * @return the entity's tamer.
	 */
	Optional<Entity> getTamer();

	/**
	 * Sets the entity's tamer.
	 *
	 * @param tamer the tamer to set.
	 */
	void setTamer(Entity tamer);

	/**
	 * @return true if the entity is tamed.
	 */
	boolean isTamed();

	/**
	 * @return true if the entity is sitting.
	 */
	boolean isSitting();

	/**
	 * Sets if the entity is sitting.
	 *
	 * @param sitting true if it's sitting.
	 */
	void setSitting(boolean sitting);

}
