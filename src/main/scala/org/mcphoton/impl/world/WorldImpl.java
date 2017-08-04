package org.mcphoton.impl.world;

import com.electronwill.utils.SimpleBag;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import org.mcphoton.Photon;
import org.mcphoton.block.BlockType;
import org.mcphoton.command.WorldCommandRegistry;
import org.mcphoton.entity.living.Player;
import org.mcphoton.event.WorldEventsManager;
import org.mcphoton.impl.command.WorldCommandRegistryImpl;
import org.mcphoton.impl.event.WorldEventsManagerImpl;
import org.mcphoton.impl.permissions.WorldPermissionsManagerImpl;
import org.mcphoton.impl.plugin.WorldPluginsManagerImpl;
import org.mcphoton.impl.world.generation.SimpleHeightmapBasedGenerator;
import org.mcphoton.permissions.WorldPermissionsManager;
import org.mcphoton.plugin.WorldPluginsManager;
import org.mcphoton.utils.Coordinates;
import org.mcphoton.world.Location;
import org.mcphoton.world.ChunkGenerator;
import org.mcphoton.world.World;
import org.mcphoton.world.WorldType;

/**
 * Implementation of World. It is thread-safe.
 *
 * @author TheElectronWill
 */
public class WorldImpl implements World {
	// World properties
	protected volatile String name;
	protected volatile File directory;
	protected volatile double spawnX = 0, spawnY = 0, spawnZ = 0;
	protected final WorldType type;

	// World managers and registries
	protected final WorldPluginsManager pluginsManager = new WorldPluginsManagerImpl(this);
	protected final WorldEventsManager eventsManager = new WorldEventsManagerImpl();
	protected final WorldCommandRegistry commandRegistry = new WorldCommandRegistryImpl();
	protected final WorldPermissionsManager permissionsManager = new WorldPermissionsManagerImpl();

	// World chunks
	protected final ChunkGenerator chunkGenerator = new SimpleHeightmapBasedGenerator(this);
	protected final ChunkCache chunkCache = new ChunkCache(this);
	protected final ChunkIO chunkIO = new ChunkIO(this);

	// World entities
	protected final Collection<Player> players = new SimpleBag<>();
	protected final Collection<Player> playersUnmodifiable = Collections.unmodifiableCollection(players);
	protected final EntitiesManager entitiesManager = new EntitiesManager();

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
		return playersUnmodifiable;
	}

	@Override
	public void save() {
		//TODO write all the ChunkColumns of the cache to the disk
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

	public ChunkCache getChunkCache() {
		return chunkCache;
	}

	public EntitiesManager getEntitiesManager() {
		return entitiesManager;
	}

	@Override
	public BlockType getBlockType(int x, int y, int z) {
		// The chunk isn't cached? Too bad => NullPointerException :'(
		int blockId = chunkCache.getCached(x / 16, z / 16).getBlockFullId(x % 16, y, z % 16);
		return Photon.getGameRegistry().getBlock(blockId);
	}

	@Override
	public BlockType getBlockType(Coordinates coords) {
		return getBlockType((int)coords.getX(), (int)coords.getY(), (int)coords.getZ());
	}
}