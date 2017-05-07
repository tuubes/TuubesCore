package org.mcphoton.server;

import java.awt.image.BufferedImage;
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
	 * @return the number of threads.
	 */
	int getThreadNumber();

	/**
	 * Sets the number of threads used to execute the tasks.
	 *
	 * @param threadNumber the number of threads
	 */
	void setThreadNumber(int threadNumber);

	/**
	 * @return the server's port
	 */
	int getPort();

	/**
	 * Sets the local port used by the server.
	 *
	 * @param port the server's port.
	 */
	void setPort(int port);

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

	/**
	 * Gets the log level. Only the messages with a "more important" or with the same level as
	 * the log level are displayed, the other are discarded. More important levels are declared
	 * first in {@link LogLevel}.java.
	 *
	 * @return the log level
	 */
	LogLevel getLogLevel();

	/**
	 * Sets the log level. Only the messages with a "more important" or with the same level as
	 * the log level are displayed, the other are discarded. More important levels are declared
	 * first in {@link LogLevel}.java.
	 *
	 * @param level the log level
	 */
	void setLogLevel(LogLevel level);
}