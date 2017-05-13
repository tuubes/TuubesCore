package org.mcphoton.event;

/**
 * Manages events for one world.
 *
 * @author TheElectronWill
 */
public interface WorldEventsManager {

	/**
	 * Registers all the events handlers defined in the specified listener object.
	 *
	 * @param listener an object that contains some event handlers definitions.
	 */
	void registerHandlers(Object listener);

	/**
	 * Unregisters all the events handlers defined in the specified listener object.
	 *
	 * @param listener an object that contains some event handlers definitions.
	 */
	void unregisterHandlers(Object listener);

	/**
	 * Registers an event handler.
	 *
	 * @param <E>          the event's type
	 * @param eventClass   the event's class
	 * @param eventHandler the handler to register
	 * @param listenOrder  the handler's order
	 */
	<E extends Event> void registerHandler(Class<E> eventClass, EventHandler<? super E> eventHandler,
										   ListenOrder listenOrder);

	/**
	 * Unregisters an event handler.
	 *
	 * @param <E>          the event's type
	 * @param eventClass   the event's class
	 * @param eventHandler the handler to register
	 * @param listenOrder  the handler's order
	 */
	<E extends Event> void unregisterHandler(Class<E> eventClass, EventHandler<? super E> eventHandler,
											 ListenOrder listenOrder);

	/**
	 * Posts an event, that is, notifies all the corresponding event handlers.
	 */
	void post(Event event);

}
