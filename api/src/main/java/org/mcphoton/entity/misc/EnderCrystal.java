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

import java.util.Optional;
import org.mcphoton.entity.Entity;
import org.mcphoton.utils.Location;

/**
 * Crystal in end that heals the EnderDragon.
 *
 * @author DJmaxZPLAY
 * @author TheElectronWill
 */
public interface EnderCrystal extends Entity {

	/**
	 * @return true if the crystal is showing the bedrock slate underneath it.
	 */
	boolean isShowingSlate();

	/**
	 * Sets if the ender crystal shows the bedrock slate underneath it.
	 *
	 * @param show true to show the slate, false to hide it.
	 */
	void setShowingSlate(boolean show);

	/**
	 * @return the location that the crystal is pointing its beam to.
	 */
	Optional<Location> getBeamTarget();

	/**
	 * Sets the location that the crystal is pointing its beam to.
	 *
	 * @param location the beam's target location.
	 */
	void setBeamTarget(Location location);

}
