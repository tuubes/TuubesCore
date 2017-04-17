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

import org.mcphoton.plugin.GlobalPlugin;

/**
 * Manages events for the GlobalPlugins.
 *
 * @author TheElectronWill
 */
public interface GlobalEventsManager {

	/**
	 * Registers all the events handlers defined in the specified listener object. The handlers are registered
	 * in every world where the GlobalPlugin is loaded.
	 *
	 * @param listener an object that contains some event handlers definitions.
	 * @param plugin   the GlobalPlugin that registers these event handlers.
	 */
	void registerHandlers(Object listener, GlobalPlugin plugin);

	/**
	 * Unregisters all the events handlers defined in the specified listener object.
	 *
	 * @param listener an object that contains some event handlers definitions.
	 * @param plugin   the GlobalPlugin that previously registered the event handlers.
	 */
	void unregisterHandlers(Object listener, GlobalPlugin plugin);

	/**
	 * Registers an event handler. The handler is registered in every world where the GlobalPlugin is loaded.
	 *
	 * @param <E>          the event's type
	 * @param eventClass   the event's class
	 * @param eventHandler the handler to register
	 * @param listenOrder  the handler's order
	 * @param plugin       the plugin that registers the handler
	 */
	<E extends Event> void registerHandler(Class<E> eventClass, EventHandler<? super E> eventHandler,
										   ListenOrder listenOrder, GlobalPlugin plugin);

	/**
	 * Unregisters an event handler.
	 *
	 * @param <E>          the event's type
	 * @param eventClass   the event's class
	 * @param eventHandler the handler to register
	 * @param listenOrder  the handler's order
	 * @param plugin       the plugin that previously registered the handler
	 */
	<E extends Event> void unregisterHandler(Class<E> eventClass, EventHandler<? super E> eventHandler,
											 ListenOrder listenOrder, GlobalPlugin plugin);

}
