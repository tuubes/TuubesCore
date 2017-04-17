/*
 * Copyright (c) 2016 MCPhoton <http://mcphoton.org> and contributors.
 *
 * This file is part of the Photon API <https://github.com/mcphoton/Photon-API>.
 *
 * The Photon API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Photon API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
