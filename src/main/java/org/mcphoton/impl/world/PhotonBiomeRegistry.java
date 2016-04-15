package org.mcphoton.impl.world;

import com.electronwill.utils.IndexMap;
import java.util.HashMap;
import java.util.Map;
import org.mcphoton.world.BiomeRegistry;
import org.mcphoton.world.BiomeType;

/**
 *
 * @author TheElectronWill
 */
public class PhotonBiomeRegistry implements BiomeRegistry {

	private final IndexMap<BiomeType> idMap = new IndexMap<>();
	private final Map<String, BiomeType> nameMap = new HashMap<>();

	@Override
	public synchronized void register(BiomeType type) {
		int id = idMap.size();
		type.initializeId(id);
		idMap.put(id, type);
		nameMap.put(type.getUniqueName(), type);
	}

	@Override
	public synchronized void register(BiomeType type, int id) {
		type.initializeId(id);
		idMap.put(id, type);
		nameMap.put(type.getUniqueName(), type);
	}

	@Override
	public synchronized BiomeType getRegistered(int id) {
		return idMap.get(id);
	}

	@Override
	public synchronized BiomeType getRegistered(String name) {
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
