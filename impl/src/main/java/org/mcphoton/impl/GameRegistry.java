package org.mcphoton.impl;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.utils.IndexMap;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;
import org.mcphoton.Photon;
import org.mcphoton.impl.block.AbstractBlockType;
import org.mcphoton.impl.entity.AbstractEntityType;
import org.mcphoton.impl.item.AbstractItemType;
import org.mcphoton.world.BiomeType;

/**
 * @author TheElectronWill
 */
@SuppressWarnings("Duplicates")
public final class GameRegistry {
	private static final File IDS_CONFIG_FILE = new File(Photon.getMainDirectory(),
														 "game_ids.toml");
	private Config idsConfig = new TomlParser().parse(IDS_CONFIG_FILE);
	private Config blocksIdsConfig = idsConfig.getValue("blocks");
	private Config objectsIdsConfig = idsConfig.getValue("objects");
	private Config mobsIdsConfig = idsConfig.getValue("mobs");
	private Config itemsIdsConfig = idsConfig.getValue("items");

	private final IndexMap<AbstractBlockType> blocksIndex = new IndexMap<>();
	private final Map<String, AbstractBlockType> blocksMap = new HashMap<>();

	private final IndexMap<AbstractEntityType> objectsIndex = new IndexMap<>();
	private final Map<String, AbstractEntityType> objectsMap = new HashMap<>();

	private final IndexMap<AbstractEntityType> mobsIndex = new IndexMap<>();
	private final Map<String, AbstractEntityType> mobsMap = new HashMap<>();

	private final IndexMap<ItemRegistration> itemsIndex = new IndexMap<>();
	private final Map<String, AbstractItemType> itemsMap = new HashMap<>();

	/**
	 * Freezes the GameRegistry: prevent any new registration and optimizes the storage of the
	 * registered data.
	 */
	public void freeze() {
		// Deletes the configuration, they're useless now
		idsConfig = null;
		blocksIdsConfig = null;
		objectsIdsConfig = null;
		mobsIdsConfig = null;
		itemsIdsConfig = null;

		// Compacts the indexes
		blocksIndex.compact();
		objectsIndex.compact();
		mobsIndex.compact();
		itemsIndex.compact();
	}

	public int registerBlock(AbstractBlockType type) {
		String uniqueName = type.getUniqueName();
		int id = blocksIdsConfig.getValue(Collections.singletonList(uniqueName));
		blocksIndex.put(id, type);
		blocksMap.put(uniqueName, type);
		return id;
	}

	public AbstractBlockType getBlock(String name) {
		return blocksMap.get(name);
	}

	public AbstractBlockType getBlock(int fullId) {
		return blocksIndex.get(fullId);
	}

	public AbstractBlockType getBlock(int id, int dataValue) {
		return getBlock(id << 4 | (dataValue & 0xf));
	}

	public ItemTypeInfos registerItem(AbstractItemType type) {
		String uniqueName = type.getUniqueName();
		Config infos = itemsIdsConfig.getValue(Collections.singletonList(uniqueName));
		int id = infos.getValue("id");
		String damageString = infos.getValue("damage");
		OptionalInt damageData;
		ItemRegistration registration;
		if (damageString == null || damageString.equals("none")) {
			damageData = OptionalInt.empty();
			registration = new BasicItem(type);
		} else {
			int dataValue = Integer.parseInt(damageString);
			damageData = OptionalInt.of(dataValue);
			registration = itemsIndex.get(id);
			if (registration == null) {
				registration = new ItemVariants();
				itemsIndex.put(id, registration);
			} else if (!(registration instanceof ItemVariants)) {
				throw new IllegalStateException("Invalid ItemType registration: cannot register "
												+ "a variant of id "
												+ id
												+ " because a basic "
												+ "type (without variant) has already been "
												+ "registered with the same id.");
			}
			((ItemVariants)registration).variants.put(dataValue, type);
		}
		itemsIndex.put(id, registration);
		itemsMap.put(uniqueName, type);
		return new ItemTypeInfos(id, damageData);
	}

	public AbstractItemType getItem(String name) {
		return itemsMap.get(name);
	}

	public AbstractItemType getItem(int id, int damageValue) {
		ItemRegistration registration = itemsIndex.get(id);
		return (registration == null) ? null : registration.getVariant(damageValue);
	}

	public int registerMob(AbstractEntityType type) {
		String uniqueName = type.getUniqueName();
		int id = mobsIdsConfig.getValue(Collections.singletonList(uniqueName));
		mobsIndex.put(id, type);
		mobsMap.put(uniqueName, type);
		return id;
	}

	public AbstractEntityType getMob(String name) {
		return mobsMap.get(name);
	}

	public AbstractEntityType getMob(int id) {
		return mobsIndex.get(id);
	}

	public int registerObject(AbstractEntityType type) {
		String uniqueName = type.getUniqueName();
		int id = objectsIdsConfig.getValue(Collections.singletonList(uniqueName));
		objectsIndex.put(id, type);
		objectsMap.put(uniqueName, type);
		return id;
	}

	public AbstractEntityType getObject(String name) {
		return objectsMap.get(name);
	}

	public AbstractEntityType getObject(int id) {
		return objectsIndex.get(id);
	}

	public static final class ItemTypeInfos {
		public final int id;
		public final OptionalInt damageData;

		public ItemTypeInfos(int id, OptionalInt damageData) {
			this.id = id;
			this.damageData = damageData;
		}
	}

	private static interface ItemRegistration {
		AbstractItemType getVariant(int damageData);
	}

	private static final class ItemVariants implements ItemRegistration {
		final IndexMap<AbstractItemType> variants = new IndexMap<>();

		@Override
		public AbstractItemType getVariant(int damageData) {
			return variants.get(damageData);
		}
	}

	private static final class BasicItem implements ItemRegistration {
		final AbstractItemType type;

		private BasicItem(AbstractItemType type) {this.type = type;}

		@Override
		public AbstractItemType getVariant(int damageData) {
			return type;
		}
	}
}