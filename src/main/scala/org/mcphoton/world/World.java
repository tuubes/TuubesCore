package org.mcphoton.world;

import java.io.File;
import java.util.Collection;
import org.mcphoton.block.BlockRef;
import org.mcphoton.block.BlockType;
import org.mcphoton.command.WorldCommandRegistry;
import org.mcphoton.entity.living.Player;
import org.mcphoton.event.WorldEventsManager;
import org.mcphoton.permissions.WorldPermissionsManager;
import org.mcphoton.plugin.WorldPluginsManager;
import org.mcphoton.utils.Coordinates;
import org.mcphoton.world.Location;

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
	 * Sets the spawn location.
	 */
	void setSpawn(Coordinates spawn);

	//---- Entities ---

	/**
	 * Gets the world's players. The returned collection is unmodifiable.
	 *
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

	//---- Blocks ----

	/**
	 * Gets the type of the block at the given coordinates, if it is currently loaded in memory.
	 * If it isn't then this method may return null.
	 *
	 * @return the block's type at (x,y,z)
	 */
	BlockType getBlockType(int x, int y, int z);

	/**
	 * Gets the type of the block at the given coordinates, if it is currently loaded in memory.
	 * If it isn't then this method may return null.
	 *
	 * @param coords the block's coordinates
	 * @return the block's type at the given coordinates
	 */
	default BlockType getBlockType(Coordinates coords) {
		return getBlockType((int)coords.getX(), (int)coords.getY(), (int)coords.getZ());
	}

	/**
	 * Returns a reference to a block.
	 *
	 * @param x the block's X coordinate
	 * @param y the block's Y coordinate
	 * @param z the block's Z coordinate
	 * @return a reference to the block at the given coordinates
	 */
	BlockRef getBlockRef(int x, int y, int z);
}