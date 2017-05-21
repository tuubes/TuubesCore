package org.mcphoton.runtime;

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
}