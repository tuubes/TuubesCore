/*
 * Copyright (c) 2016 MCPhoton <http://mcphoton.org> and contributors.
 *
 * This file is part of the Photon Server Implementation <https://github.com/mcphoton/Photon-Server>.
 *
 * The Photon Server Implementation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Photon Server Implementation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mcphoton.impl.event;

import org.mcphoton.event.Event;
import org.mcphoton.event.EventHandler;
import org.mcphoton.event.ListenOrder;
import org.mcphoton.event.GlobalEventsManager;
import org.mcphoton.event.WorldEventsManager;
import org.mcphoton.plugin.GlobalPlugin;
import org.mcphoton.world.World;

/**
 * Implementation of the {@link GlobalEventsManager}. It internally uses the {@link WorldEventsManager}.
 *
 * @author TheElectronWill
 */
public class GlobalEventsManagerImpl implements GlobalEventsManager {

	@Override
	public void registerHandlers(Object listener, GlobalPlugin plugin) {
		for (World world : plugin.getActiveWorlds()) {
			world.getEventsManager().registerHandlers(listener);
		}
		//TODO optimisation possible : récupérer les informations de l'objet listener (annotations, méthodes, etc.) une fois seulement,
		//et enregistrer des listeners pour chaque monde à partir de ces informations.
	}

	@Override
	public void unregisterHandlers(Object listener, GlobalPlugin plugin) {
		for (World world : plugin.getActiveWorlds()) {
			world.getEventsManager().unregisterHandlers(listener);
		}
	}

	@Override
	public <E extends Event> void registerHandler(Class<E> eventClass, EventHandler<? super E>
			eventHandler, ListenOrder listenOrder, GlobalPlugin plugin) {
		for (World world : plugin.getActiveWorlds()) {
			world.getEventsManager().registerHandler(eventClass, eventHandler, listenOrder);
		}
	}

	@Override
	public <E extends Event> void unregisterHandler(Class<E> eventClass, EventHandler<? super E>
			eventHandler, ListenOrder listenOrder, GlobalPlugin plugin) {
		for (World world : plugin.getActiveWorlds()) {
			world.getEventsManager().unregisterHandler(eventClass, eventHandler, listenOrder);
		}
	}

}
