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

import java.util.Optional;
import org.mcphoton.block.BlockData;

/**
 * A minecart.
 *
 * @author DJmaxZPLAY
 * @author TheElectronWill
 */
public interface Minecart extends Vehicle {

	/**
	 * @return the current shaking force.
	 */
	int getShakingForce();

	/**
	 * Sets the minecart's shaking force.
	 *
	 * @param force the shaking force, in arbitrary units (we still need to figure out how it works).
	 */
	void setShakingForce(int force);

	/**
	 * @return the block in the minecart.
	 */
	Optional<BlockData> getDisplayBlock();

	/**
	 * Sets the blocks that displays in the minecart.
	 *
	 * @param block the block in the minecart.
	 */
	void setDisplayBlock(BlockData block);

	/**
	 * @return the y position of the block that displays in the minecart, in arbitrary units.
	 */
	int getDisplayBlockPosition();

}
