package org.mcphoton.impl.entity;

import com.electronwill.utils.IndexMap;
import java.util.HashMap;
import java.util.Map;
import org.mcphoton.entity.EntityRegistry;
import org.mcphoton.entity.EntityType;

/**
 *
 * @author TheElectronWill
 */
public class PhotonEntityRegistry implements EntityRegistry {

	private final IndexMap<EntityType> idMap = new IndexMap<>();
	private final Map<String, EntityType> nameMap = new HashMap<>();

	@Override
	public synchronized void register(EntityType type) {
		int id = idMap.size();
		type.initializeId(id);
		idMap.put(id, type);
		nameMap.put(type.getUniqueName(), type);
	}

	@Override
	public synchronized void register(EntityType type, int id) {
		type.initializeId(id);
		idMap.put(id, type);
		nameMap.put(type.getUniqueName(), type);
	}

	@Override
	public synchronized EntityType getRegistered(int id) {
		return idMap.get(id);
	}

	@Override
	public synchronized EntityType getRegistered(String name) {
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
