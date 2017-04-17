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
package org.mcphoton.entity.vehicle;

import java.util.List;
import org.mcphoton.entity.Entity;

/**
 * A vehicle that can carry passengers.
 *
 * @author TheElectronWill
 */
public interface Vehicle {

	/**
	 * @return the list of the vehicle's passengers. Modifying this list doesn't update the vehicle on the
	 * client side and doesn't update the added/removed passenger entities.
	 */
	List<Entity> getPassengers();

	/**
	 * Sets the vehicle's passengers, and updates the vehicle and the passengers.
	 *
	 * @param passengers the passengers to set.
	 */
	void setPassengers(Entity... passengers);

	/**
	 * Adds a passenger to the vehicle, and updates the vehicle and the passengers.
	 *
	 * @param passenger the passenger to add.
	 */
	void addPassenger(Entity passenger);

	/**
	 * Removes a passenger from the vehicle, and updates the vehicle and the passengers.
	 *
	 * @param passenger the passenger to remove.
	 */
	void removePassenger(Entity passenger);

}
