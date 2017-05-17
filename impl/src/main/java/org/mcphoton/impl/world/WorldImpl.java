package org.mcphoton.impl.world;

import com.electronwill.utils.Bag;
import com.electronwill.utils.IndexMap;
import com.electronwill.utils.SimpleBag;
import java.io.File;
import java.util.Collection;
import org.mcphoton.Photon;
import org.mcphoton.command.WorldCommandRegistry;
import org.mcphoton.entity.Entity;
import org.mcphoton.entity.living.Player;
import org.mcphoton.event.WorldEventsManager;
import org.mcphoton.impl.command.WorldCommandRegistryImpl;
import org.mcphoton.impl.entity.AbstractEntity;
import org.mcphoton.impl.event.WorldEventsManagerImpl;
import org.mcphoton.impl.permissions.WorldPermissionsManagerImpl;
import org.mcphoton.impl.plugin.WorldPluginsManagerImpl;
import org.mcphoton.impl.world.generation.SimpleHeightmapBasedGenerator;
import org.mcphoton.permissions.WorldPermissionsManager;
import org.mcphoton.plugin.WorldPluginsManager;
import org.mcphoton.utils.Location;
import org.mcphoton.world.ChunkGenerator;
import org.mcphoton.world.World;
import org.mcphoton.world.WorldType;
import org.mcphoton.world.protection.WorldAccessManager;

/**
 * Implementation of World. It is thread-safe.
 *
 * @author TheElectronWill
 */
public class WorldImpl implements World {
	protected volatile String name;
	protected volatile File directory;
	protected volatile double spawnX = 0, spawnY = 0, spawnZ = 0;

	protected final WorldType type;
	protected final Collection<Player> players = new SimpleBag<>();
	protected final IndexMap<AbstractEntity> entities = new IndexMap<>();// ids of the world's entities.
	protected final Bag<Integer> removedIds = new SimpleBag<>(100, 50);// ids of the removed
	// entities. Reusing them avoids fragmentation.

	protected final WorldPluginsManager pluginsManager = new WorldPluginsManagerImpl(this);
	protected final WorldEventsManager eventsManager = new WorldEventsManagerImpl();
	protected final WorldCommandRegistry commandRegistry = new WorldCommandRegistryImpl();
	protected volatile ChunkGenerator chunkGenerator = new SimpleHeightmapBasedGenerator(this);
	protected volatile WorldAccessManager accessManager = new OpenWorldAccessManager(this);
	protected final WorldChunksManager chunksManager = new WorldChunksManager(this);
	protected final WorldPermissionsManager permissionsManager = new WorldPermissionsManagerImpl();

	public WorldImpl(String name, WorldType type) {
		this.name = name;
		this.type = type;
		this.directory = new File(Photon.getWorldsDirectory(), name);
		this.directory.mkdir();
	}

	public WorldImpl(File directory, WorldType type) {
		this.name = directory.getName();
		this.type = type;
		this.directory = directory;
	}

	public AbstractEntity getEntity(int entityId) {
		synchronized (entities) {
			return entities.get(entityId);
		}
	}

	public void addEntity(AbstractEntity entity) {
		if (entity.getLocation().getWorld() != this) {
			throw new IllegalArgumentException(
					"An entity can only be added to the world it is " + "in");
		}
		synchronized (entities) {
			int nextId;
			if (removedIds.isEmpty()) {
				nextId = entities.size();// need a new id
			} else {
				nextId = removedIds.get(0);// reuse this id
				removedIds.remove(0);
			}
			entity.init(nextId);
			entities.put(nextId, entity);
		}
	}

	public void removeEntity(int entityId) {
		synchronized (entities) {
			entities.remove(entityId);
			removedIds.add(entityId);
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public synchronized void renameTo(String name) {
		boolean success = directory.renameTo(new File(Photon.getWorldsDirectory(), name));
		if (success) {
			this.name = name;
		}
	}

	@Override
	public File getDirectory() {
		return directory;
	}

	@Override
	public WorldType getType() {
		return type;
	}

	@Override
	public Location getSpawn() {
		return new Location(spawnX, spawnY, spawnZ, this);
	}

	@Override
	public synchronized void setSpawn(double x, double y, double z) {
		this.spawnX = x;
		this.spawnY = y;
		this.spawnZ = z;
	}

	@Override
	public Collection<Player> getPlayers() {
		return players;
	}

	@Override
	public void save() {
		chunksManager.writeAll();
	}

	@Override
	public void delete() {
		directory.delete();
	}

	@Override
	public WorldPermissionsManager getPermissionsManager() {
		return permissionsManager;
	}

	@Override
	public WorldAccessManager getAccessManager() {
		return accessManager;
	}

	@Override
	public void setAccessManager(WorldAccessManager manager) {
		this.accessManager = manager;
	}

	@Override
	public WorldCommandRegistry getCommandRegistry() {
		return commandRegistry;
	}

	@Override
	public WorldEventsManager getEventsManager() {
		return eventsManager;
	}

	@Override
	public WorldPluginsManager getPluginsManager() {
		return pluginsManager;
	}

	@Override
	public ChunkGenerator getChunkGenerator() {
		return chunkGenerator;
	}

	@Override
	public void setChunkGenerator(ChunkGenerator generator) {
		this.chunkGenerator = generator;
	}

	public WorldChunksManager getChunksManager() {
		return chunksManager;
	}
}