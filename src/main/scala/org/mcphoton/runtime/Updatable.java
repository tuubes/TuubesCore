package org.mcphoton.runtime;

import java.io.IOException;

/**
 * @author TheElectronWill
 */
public interface Updatable {
	/**
	 * Performs an update.
	 *
	 * @param dt the elapsed time, in seconds, since the last execution of the update loop
	 */
	void update(double dt);

	/**
	 * Destroys this updatable and release all associated resources.
	 */
	void destroy();

	/**
	 * Checks if this Updatable has been destroyed. A destroyed Updatable should not be updated
	 * nor used.
	 *
	 * @return true if this updatable has been destroyed, false otherwise
	 */
	boolean isDestroyed();

	/**
	 * Sends the changes to the clients.
	 */
	void sendUpdates() throws IOException;
}