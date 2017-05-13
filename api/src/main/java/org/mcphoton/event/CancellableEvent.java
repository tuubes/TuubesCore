package org.mcphoton.event;

/**
 * An event that may be cancelled. If an event is cancelled, its normal actions will not be executed.
 *
 * @author TheElectronWill
 *
 */
public interface CancellableEvent extends Event {

	boolean isCancelled();

	void setCancelled(boolean cancelled);

}
