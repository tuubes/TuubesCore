package org.mcphoton.impl.item;

import com.electronwill.utils.IndexMap;
import java.util.HashMap;
import java.util.Map;
import org.mcphoton.item.ItemRegistry;
import org.mcphoton.item.ItemType;

/**
 *
 * @author TheElectronWill
 */
public class PhotonItemRegistry implements ItemRegistry {

	private final IndexMap<ItemType> idMap = new IndexMap<>();
	private final Map<String, ItemType> nameMap = new HashMap<>();

	@Override
	public synchronized void register(ItemType type) {
		int id = idMap.size();
		type.initializeId(id);
		idMap.put(id, type);
		nameMap.put(type.getUniqueName(), type);
	}

	@Override
	public synchronized void register(ItemType type, int id) {
		type.initializeId(id);
		idMap.put(id, type);
		nameMap.put(type.getUniqueName(), type);
	}

	@Override
	public synchronized ItemType getRegistered(int id) {
		return idMap.get(id);
	}

	@Override
	public synchronized ItemType getRegistered(String name) {
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
