package org.mcphoton.impl.runtime;

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
}