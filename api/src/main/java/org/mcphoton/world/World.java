/*
 * Copyright (c) 2016 MCPhoton <http://mcphoton.org> and contributors.
 *
 * This file is part of the Photon API <https://github.com/mcphoton/Photon-API>.
 *
 * The Photon API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Photon API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mcphoton.world;

import java.io.File;
import java.util.Collection;
import java.util.Optional;
import org.mcphoton.command.WorldCommandRegistry;
import org.mcphoton.entity.Entity;
import org.mcphoton.entity.living.Player;
import org.mcphoton.event.WorldEventsManager;
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
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @param z the z coordinate.
	 */
	void spawnEntity(Entity entity, double x, double y, double z);

	/**
	 * Spawns an entity at the given location.
	 *
	 * @param entity the entity to spawn.
	 * @param loc the location.
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

	//---- Misc ----
	/**
	 * @return the players currently in this world.
	 */
	Collection<Player> getPlayers();

	/**
	 * Saves this world.
	 */
	void save();

	/**
	 * Deletes this world.
	 */
	void delete();

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
	 * @param area the area to access.
	 * @param accessor the Object that wants to access this area.
	 * @return an UnlockedAreaAccess, optional.
	 */
	default Optional<UnlockedAreaAccess> accessArea(Area area, Object accessor) {
		return getAccessManager().unlockArea(area, accessor);
	}

	/**
	 * Accesses a world chunk.
	 *
	 * @param x the chunk's x coordinate.
	 * @param z the chunk's z coordinate.
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
