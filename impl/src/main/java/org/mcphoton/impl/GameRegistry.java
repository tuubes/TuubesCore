package org.mcphoton.impl;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.utils.ConcurrentIndexMap;
import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.OptionalInt;
import java.util.concurrent.ConcurrentHashMap;
import org.mcphoton.Photon;
import org.mcphoton.impl.block.AbstractBlockType;
import org.mcphoton.impl.entity.mobs.AbstractMobType;
import org.mcphoton.impl.entity.objects.AbstractObjectType;
import org.mcphoton.impl.item.AbstractItemType;
import org.mcphoton.impl.world.AbstractBiomeType;

/**
 * Regitry for game types: blocks, "object" entities, "mob" entities, items, biomes. The IDs are
 * defined in the config file "game_ids.toml".
 * <p>
 * The get and freeze operations are safe to use from multiple thread. The register
 * operations should be safe if the same type is never registered twice concurrently.
 *
 * @author TheElectronWill
 */
@SuppressWarnings("Duplicates")
public final class GameRegistry {
	private static final File IDS_CONFIG_FILE = new File(Photon.getMainDirectory(), "game_ids.toml");
	private Config idsConfig = new TomlParser().parse(IDS_CONFIG_FILE);
	private Config blocksIdsConfig = idsConfig.getValue("blocks");
	private Config objectsIdsConfig = idsConfig.getValue("objects");
	private Config mobsIdsConfig = idsConfig.getValue("mobs");
	private Config itemsIdsConfig = idsConfig.getValue("items");
	private Config biomesIdsConfig = idsConfig.getValue("items");
	/* Note: we don't need to synchronize on the configs because we only perform reading
	operations, and they are initialized at the beginning and never modified after. */

	private final ConcurrentIndexMap<AbstractBlockType> blocksIndex = new ConcurrentIndexMap<>();
	private final Map<String, AbstractBlockType> blocksMap = new ConcurrentHashMap<>();

	private final ConcurrentIndexMap<AbstractObjectType> objectsIndex = new ConcurrentIndexMap<>();
	private final Map<String, AbstractObjectType> objectsMap = new ConcurrentHashMap<>();

	private final ConcurrentIndexMap<AbstractMobType> mobsIndex = new ConcurrentIndexMap<>();
	private final Map<String, AbstractMobType> mobsMap = new ConcurrentHashMap<>();

	private final ConcurrentIndexMap<ItemRegistration> itemsIndex = new ConcurrentIndexMap<>();
	private final Map<String, AbstractItemType> itemsMap = new ConcurrentHashMap<>();

	private final ConcurrentIndexMap<AbstractBiomeType> biomesIndex = new ConcurrentIndexMap<>();
	private final Map<String, AbstractBiomeType> biomesMap = new ConcurrentHashMap<>();

	/**
	 * Freezes the GameRegistry: prevent any new registration and optimizes the storage of the
	 * registered data.
	 */
	public void freeze() {
		// Deletes the configurations, they're useless now
		idsConfig = null;
		blocksIdsConfig = null;
		objectsIdsConfig = null;
		mobsIdsConfig = null;
		itemsIdsConfig = null;
		biomesIdsConfig = null;

		// Compacts the indexes
		blocksIndex.compact();
		objectsIndex.compact();
		mobsIndex.compact();
		itemsIndex.compact();
		biomesIndex.compact();
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

	public int registerMob(AbstractMobType type) {
		String uniqueName = type.getUniqueName();
		int id = mobsIdsConfig.getValue(Collections.singletonList(uniqueName));
		mobsIndex.put(id, type);
		mobsMap.put(uniqueName, type);
		return id;
	}

	public AbstractMobType getMob(String name) {
		return mobsMap.get(name);
	}

	public AbstractMobType getMob(int id) {
		return mobsIndex.get(id);
	}

	public int registerObject(AbstractObjectType type) {
		String uniqueName = type.getUniqueName();
		int id = objectsIdsConfig.getValue(Collections.singletonList(uniqueName));
		objectsIndex.put(id, type);
		objectsMap.put(uniqueName, type);
		return id;
	}

	public AbstractObjectType getObject(String name) {
		return objectsMap.get(name);
	}

	public AbstractObjectType getObject(int id) {
		return objectsIndex.get(id);
	}

	public int registerBiome(AbstractBiomeType type) {
		String uniqueName = type.getUniqueName();
		int id = biomesIdsConfig.getValue(Collections.singletonList(uniqueName));
		biomesIndex.put(id, type);
		biomesMap.put(uniqueName, type);
		return id;
	}

	public AbstractBiomeType getBiome(String name) {
		return biomesMap.get(name);
	}

	public AbstractBiomeType getBiome(int id) {
		return biomesIndex.get(id);
	}

	/**
	 * Result of {@link #registerItem(AbstractItemType)}. Contains the informations about the
	 * registered item type: its ID and its damageData if the type has variants.
	 */
	public static final class ItemTypeInfos {
		public final int id;
		public final OptionalInt damageData;

		public ItemTypeInfos(int id, OptionalInt damageData) {
			this.id = id;
			this.damageData = damageData;
		}
	}

	/**
	 * Registration infos of a type of item.
	 */
	private static interface ItemRegistration {
		AbstractItemType getVariant(int damageData);
	}

	/**
	 * Registration infos of an item type that has mutliple variants (like Stone).
	 */
	private static final class ItemVariants implements ItemRegistration {
		final ConcurrentIndexMap<AbstractItemType> variants = new ConcurrentIndexMap<>();

		@Override
		public AbstractItemType getVariant(int damageData) {
			return variants.get(damageData);
		}
	}

	/**
	 * Registration info of a simple item type that has no variants.
	 */
	private static final class BasicItem implements ItemRegistration {
		final AbstractItemType type;

		private BasicItem(AbstractItemType type) {this.type = type;}

		@Override
		public AbstractItemType getVariant(int damageData) {
			return type;
		}
	}
}