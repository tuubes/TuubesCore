package org.mcphoton.world;

import java.io.File;
import java.util.Collection;
import java.util.Optional;
import org.mcphoton.command.WorldCommandRegistry;
import org.mcphoton.entity.Entity;
import org.mcphoton.entity.living.Player;
import org.mcphoton.event.WorldEventsManager;
import org.mcphoton.permissions.WorldPermissionsManager;
import org.mcphoton.plugin.WorldPluginsManager;
import org.mcphoton.utils.Location;
import org.mcphoton.world.areas.Area;
import org.mcphoton.world.protection.CheckedWorldAccess;
import org.mcphoton.world.protection.UnlockedAreaAccess;
import org.mcphoton.world.protection.UnlockedWorldAccess;
import org.mcphoton.world.protection.WorldAccessManager;

/**
 * A game world.
 *
 * @author TheElectronWill
 */
public interface World {
	//---- Properties ----

	/**
	 * @return the world's name.
	 */
	String getName();

	/**
	 * Renames the world.
	 *
	 * @param name the new name.
	 */
	void renameTo(String name);

	/**
	 * Saves this world.
	 */
	void save();

	/**
	 * Deletes this world.
	 */
	void delete();

	/**
	 * @return the world's directory.
	 */
	File getDirectory();

	/**
	 * @return the world's type.
	 */
	WorldType getType();

	/**
	 * @return the world's spawn lcoation.
	 */
	Location getSpawn();

	/**
	 * Sets the world's spawn.
	 *
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @param z the z coordinate.
	 */
	void setSpawn(double x, double y, double z);

	/**
	 * Sets the world's spawn.
	 *
	 * @param spawn the spawn's location.
	 */
	default void setSpawn(Location spawn) {
		setSpawn(spawn.getX(), spawn.getY(), spawn.getZ());
	}

	//---- Entities ---

	/**
	 * Spawns an entity at the given coordinates.
	 *
	 * @param entity the entity to spawn.
	 * @param x      the x coordinate.
	 * @param y      the y coordinate.
	 * @param z      the z coordinate.
	 */
	void spawnEntity(Entity entity, double x, double y, double z);

	/**
	 * Spawns an entity at the given location.
	 *
	 * @param entity the entity to spawn.
	 * @param loc    the location.
	 */
	default void spawnEntity(Entity entity, Location loc) {
		spawnEntity(entity, loc.getX(), loc.getY(), loc.getZ());
	}

	/**
	 * Removes (deletes) an entity from this world.
	 *
	 * @param entity the entity to remove.
	 */
	void removeEntity(Entity entity);

	/**
	 * Removes (deletes) an entity from this world.
	 *
	 * @param entityId the id of the eneity to remove.
	 */
	void removeEntity(int entityId);

	/**
	 * Gets an entity by id.
	 *
	 * @param entityId the entity's id.
	 * @return the entity of this world that has the specified id.
	 */
	Entity getEntity(int entityId);

	/**
	 * @return the players currently in this world.
	 */
	Collection<Player> getPlayers();

	//---- Registries and Managers ----

	/**
	 * @return the world's command registry.
	 */
	WorldCommandRegistry getCommandRegistry();

	/**
	 * @return the world's events manager.
	 */
	WorldEventsManager getEventsManager();

	/**
	 * @return the world's plugins manager.
	 */
	WorldPluginsManager getPluginsManager();

	/**
	 * @return the world's permissions manager.
	 */
	WorldPermissionsManager getPermissionsManager();

	//---- World generation ----

	/**
	 * @return the world's chunk generator.
	 */
	ChunkGenerator getChunkGenerator();

	/**
	 * Sets the world's chunk generator.
	 *
	 * @param generator the generator to set.
	 */
	void setChunkGenerator(ChunkGenerator generator);

	//---- Block Access ----

	/**
	 * @return the world's access manager.
	 */
	WorldAccessManager getAccessManager();

	/**
	 * Sets the world's access manager.
	 *
	 * @param manager the access manager to set.
	 */
	void setAccessManager(WorldAccessManager manager);

	/**
	 * Accesses the world's blocks.
	 *
	 * @return a CheckedWorldAccess.
	 */
	default CheckedWorldAccess access() {
		return getAccessManager().getAccess();
	}

	/**
	 * Accesses a world area.
	 *
	 * @param area     the area to access.
	 * @param accessor the Object that wants to access this area.
	 * @return an UnlockedAreaAccess, optional.
	 */
	default Optional<UnlockedAreaAccess> accessArea(Area area, Object accessor) {
		return getAccessManager().unlockArea(area, accessor);
	}

	/**
	 * Accesses a world chunk.
	 *
	 * @param x        the chunk's x coordinate.
	 * @param z        the chunk's z coordinate.
	 * @param accessor the Object that wants to access this chunk.
	 * @return a ChunkColumn, optional.
	 */
	default Optional<ChunkColumn> accessChunk(int x, int z, Object accessor) {
		return getAccessManager().unlockChunk(x, z, accessor);
	}

	/**
	 * Unlocks the entire world for an unlimited block access.
	 *
	 * @param accessor the Object that wants to access this chunk.
	 * @return an UnlockedWorldAccess, optional.
	 */
	default Optional<UnlockedWorldAccess> accessAll(Object accessor) {
		return getAccessManager().unlockWorld(accessor);
	}
}