package org.mcphoton.server;

import java.util.Collection;
import java.util.UUID;
import org.mcphoton.entity.living.Player;
import org.mcphoton.world.World;

/**
 * Represents a game server.
 */
public interface Server {
	/**
	 * @return the whitelist.
	 */
	AccessList getWhitelist();

	/**
	 * @return the blacklist.
	 */
	AccessList getBlacklist();

	/**
	 * Gets all the currently connected players.
	 *
	 * @return an unmodifiable collection containing the currently online players.
	 */
	Collection<Player> getOnlinePlayers();

	/**
	 * Gets an online Player.
	 *
	 * @param accountId the player's account id
	 * @return an online Player instance
	 */
	Player getPlayer(UUID accountId);

	/**
	 * Gets an online Player.
	 *
	 * @param name the player's name
	 * @return an online Player instance
	 */
	Player getPlayer(String name);

	/**
	 * Gets all the server's worlds.
	 *
	 * @return an unmodifiable collection containing the server's worlds.
	 */
	Collection<World> getWorlds();

	/**
	 * Gets the world with the specified name.
	 *
	 * @param name the world's name.
	 * @return the world with the specified name.
	 */
	World getWorld(String name);

	/**
	 * @return the server's configuration.
	 */
	ServerConfiguration getConfiguration();
}