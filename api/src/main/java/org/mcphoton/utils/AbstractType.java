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
package org.mcphoton.utils;

import com.electronwill.utils.IntConstant;

/**
 * Abstract class for types (block types, item types, etc.)
 *
 * @author TheElectronWill
 */
public abstract class AbstractType implements Type {

	private final IntConstant id = new IntConstant();

	@Override
	public int getId() {
		return id.get();
	}

	/**
	 * Initializes the unique id of this type. This method may only be called once.
	 */
	public void initializeId(int id) {
		this.id.init(id);
	}

	@Override
	public int hashCode() {
		return id.get();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final AbstractType other = (AbstractType) obj;
		return getId() == other.getId();
	}

}
