/* 
 * Copyright (c) 2016 MCPhoton <http://mcphoton.org> and contributors.
 * 
 * This file is part of the Photon Server Implementation <https://github.com/mcphoton/Photon-Server>.
 * 
 * The Photon Server Implementation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Photon Server Implementation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mcphoton.impl.block;

import com.electronwill.utils.IndexMap;
import java.util.HashMap;
import java.util.Map;
import org.mcphoton.block.BlockRegistry;
import org.mcphoton.block.BlockType;

/**
 *
 * @author TheElectronWill
 */
public class PhotonBlockRegistry implements BlockRegistry {

	private final IndexMap<BlockType> idMap = new IndexMap<>();
	private final Map<String, BlockType> nameMap = new HashMap<>();

	@Override
	public synchronized void register(BlockType type) {
		int id = idMap.size();
		type.initializeId(id);
		idMap.put(id, type);
		nameMap.put(type.getUniqueName(), type);
	}

	@Override
	public synchronized void register(BlockType type, int id) {
		type.initializeId(id);
		idMap.put(id, type);
		nameMap.put(type.getUniqueName(), type);
	}

	@Override
	public synchronized BlockType getRegistered(int id) {
		return idMap.get(id);
	}

	@Override
	public synchronized BlockType getRegistered(String name) {
		return nameMap.get(name);
	}

	@Override
	public synchronized boolean isRegistered(int id) {
		return idMap.containsKey(id);
	}

	@Override
	public synchronized boolean isRegistered(String name) {
		return nameMap.containsKey(name);
	}

	public int getBiggestBlockId() {
		return Math.max(0, idMap.array().length - 1);
	}

}
