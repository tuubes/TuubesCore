package org.mcphoton.server;

import java.awt.image.BufferedImage;
import java.net.InetSocketAddress;
import org.mcphoton.utils.Location;

/**
 * Represents the server's configuration.
 *
 * @author TheElectronWill
 */
public interface ServerConfiguration {

	/**
	 * @return the message of the day.
	 */
	String getMOTD();

	/**
	 * Sets the motd.
	 *
	 * @param motd the message of the day.
	 */
	void setMOTD(String motd);

	/**
	 * @return the maximum number of players that can connected to the server.
	 */
	int getMaxPlayers();

	/**
	 * Sets the maximum number of players that can be connected to the server.
	 *
	 * @param maxPlayers the max number of connected players.
	 */
	void setMaxPlayers(int maxPlayers);

	/**
	 * @return the server's address.
	 */
	InetSocketAddress getAddress();

	/**
	 * Sets the server's address.
	 *
	 * @param address the server's address.
	 */
	void setAddress(InetSocketAddress address);

	/**
	 * @return the server's icon.
	 */
	BufferedImage getIcon();

	/**
	 * Sets the server's icon.
	 *
	 * @param icon the icon to set.
	 */
	void setIcon(BufferedImage icon);

	/**
	 * @return the spawn location.
	 */
	Location getSpawnLocation();

	/**
	 * Sets the spawn location.
	 *
	 * @param location the spawn location to set.
	 */
	void setSpawnLocation(Location location);

	/**
	 * Checks if this server is in online mode.
	 *
	 * @return {@code true} if it is in online mode, {@code false} if it isn't.
	 */
	boolean isOnlineMode();

	/**
	 * Sets if this server is in online mode or not.
	 *
	 * @param online {@code true} if it is in online mode, {@code false} if it isn't.
	 */
	void setOnlineMode(boolean online);

}
