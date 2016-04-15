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

}
