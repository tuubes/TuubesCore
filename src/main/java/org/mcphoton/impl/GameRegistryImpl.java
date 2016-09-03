package org.mcphoton.impl;

import com.electronwill.utils.IndexMap;
import java.util.HashMap;
import java.util.Map;
import org.mcphoton.GameRegistry;
import org.mcphoton.block.BlockType;
import org.mcphoton.entity.EntityType;
import org.mcphoton.item.ItemType;
import org.mcphoton.world.BiomeType;

/**
 *
 * @author TheElectronWill
 */
public final class GameRegistryImpl implements GameRegistry {

	private final IndexMap<BiomeType> biomesIds = new IndexMap<>();
	private final Map<String, BiomeType> biomesNames = new HashMap<>();

	private final IndexMap<BlockType> blocksIds = new IndexMap<>();
	private final Map<String, BlockType> blocksNames = new HashMap<>();

	private final IndexMap<EntityType> entitiesIds = new IndexMap<>();
	private final Map<String, EntityType> entitiesNames = new HashMap<>();
	private final IndexMap<ItemType> itemsIds = new IndexMap<>();
	private final Map<String, ItemType> itemsNames = new HashMap<>();

	@Override
	public BiomeType getRegisteredBiome(int id) {
		synchronized (biomesIds) {
			return biomesIds.get(id);
		}
	}

	@Override
	public BiomeType getRegisteredBiome(String name) {
		synchronized (biomesIds) {
			return biomesNames.get(name);
		}
	}

	@Override
	public BlockType getRegisteredBlock(int id) {
		synchronized (blocksIds) {
			return blocksIds.get(id);
		}
	}

	@Override
	public BlockType getRegisteredBlock(String name) {
		synchronized (blocksIds) {
			return blocksNames.get(name);
		}
	}

	@Override
	public EntityType getRegisteredEntity(int id) {
		synchronized (entitiesIds) {
			return entitiesIds.get(id);
		}
	}

	@Override
	public EntityType getRegisteredEntity(String name) {
		synchronized (entitiesIds) {
			return entitiesNames.get(name);
		}
	}

	@Override
	public ItemType getRegisteredItem(int id) {
		synchronized (itemsIds) {
			return itemsIds.get(id);
		}
	}

	@Override
	public ItemType getRegisteredItem(String name) {
		synchronized (itemsIds) {
			return itemsNames.get(name);
		}
	}

	@Override
	public boolean isBiomeRegistered(int id) {
		synchronized (biomesIds) {
			return biomesIds.containsKey(id);
		}
	}

	@Override
	public boolean isBiomeRegistered(String name) {
		synchronized (biomesIds) {
			return biomesNames.containsKey(name);
		}
	}

	@Override
	public boolean isBlockRegistered(int id) {
		synchronized (blocksIds) {
			return blocksIds.containsKey(id);
		}
	}

	@Override
	public boolean isBlockRegistered(String name) {
		synchronized (blocksIds) {
			return blocksNames.containsKey(name);
		}
	}

	@Override
	public boolean isEntityRegistered(int id) {
		synchronized (entitiesIds) {
			return entitiesIds.containsKey(id);
		}
	}

	@Override
	public boolean isEntityRegistered(String name) {
		synchronized (entitiesIds) {
			return entitiesNames.containsKey(name);
		}
	}

	@Override
	public boolean isItemRegistered(int id) {
		synchronized (itemsIds) {
			return itemsIds.containsKey(id);
		}
	}

	@Override
	public boolean isItemRegistered(String name) {
		synchronized (itemsIds) {
			return itemsNames.containsKey(name);
		}
	}

	@Override
	public int registerBiome(BiomeType type) {
		synchronized (biomesIds) {
			int id = biomesIds.size();
			type.initializeId(id);
			biomesIds.put(id, type);
			biomesNames.put(type.getUniqueName(), type);
			return id;
		}
	}

	@Override
	public void registerBiome(BiomeType type, int id) {
		synchronized (biomesIds) {
			type.initializeId(id);
			biomesIds.put(id, type);
			biomesNames.put(type.getUniqueName(), type);
		}
	}

	@Override
	public int registerBlock(BlockType type) {
		synchronized (blocksIds) {
			int id = blocksIds.size();
			type.initializeId(id);
			blocksIds.put(id, type);
			blocksNames.put(type.getUniqueName(), type);
			return id;
		}
	}

	@Override
	public void registerBlock(BlockType type, int id) {
		synchronized (blocksIds) {
			type.initializeId(id);
			blocksIds.put(id, type);
			blocksNames.put(type.getUniqueName(), type);
		}
	}

	@Override
	public int registerEntity(EntityType type) {
		synchronized (entitiesIds) {
			int id = entitiesIds.size();
			type.initializeId(id);
			entitiesIds.put(id, type);
			entitiesNames.put(type.getUniqueName(), type);
			return id;
		}
	}

	@Override
	public void registerEntity(EntityType type, int id) {
		synchronized (entitiesIds) {
			type.initializeId(id);
			entitiesIds.put(id, type);
			entitiesNames.put(type.getUniqueName(), type);
		}
	}

	@Override
	public int registerItem(ItemType type) {
		synchronized (itemsIds) {
			int id = itemsIds.size();
			type.initializeId(id);
			itemsIds.put(id, type);
			itemsNames.put(type.getUniqueName(), type);
			return id;
		}
	}

	@Override
	public void registerItem(ItemType type, int id) {
		synchronized (itemsIds) {
			type.initializeId(id);
			itemsIds.put(id, type);
			itemsNames.put(type.getUniqueName(), type);
		}
	}

}
