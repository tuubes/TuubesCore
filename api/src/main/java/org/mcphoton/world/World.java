package org.mcphoton.world;

import java.io.File;
import java.util.Collection;
import java.util.Optional;
import org.mcphoton.block.BlockType;
import org.mcphoton.command.WorldCommandRegistry;
import org.mcphoton.entity.living.Player;
import org.mcphoton.event.WorldEventsManager;
import org.mcphoton.permissions.WorldPermissionsManager;
import org.mcphoton.plugin.WorldPluginsManager;
import org.mcphoton.utils.Coordinates;
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

	BlockType getBlockType(int x, int y, int z);

	BlockType getBlockType(Coordinates coords);
}