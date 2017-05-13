package org.mcphoton.event;

/**
 * Represents an event. Events may be posted to the listeners via the {@link EventsManager}. Events are posted
 * and should be created and used in a worker thread, ie a Thread that is part of the server's
 * ScheduledExecutorService (see the Photon's thread architecture).
 *
 * @author TheElectronWill
 *
 */
public interface Event {

}
