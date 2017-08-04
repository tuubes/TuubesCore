package org.mcphoton.event;

import org.mcphoton.event.Event;
import org.mcphoton.event.EventHandler;
import org.mcphoton.event.ListenOrder;
import org.mcphoton.event.GlobalEventsManager;
import org.mcphoton.event.WorldEventsManager;
import org.mcphoton.plugin.GlobalPlugin;
import org.mcphoton.world.World;

/**
 * Implementation of the {@link GlobalEventsManager}. It internally uses the {@link
 * WorldEventsManager}.
 *
 * @author TheElectronWill
 */
public final class GlobalEventsManagerImpl implements GlobalEventsManager {

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
	public <E extends Event> void registerHandler(Class<E> eventClass,
												  EventHandler<? super E> eventHandler,
												  ListenOrder listenOrder, GlobalPlugin plugin) {
		for (World world : plugin.getActiveWorlds()) {
			world.getEventsManager().registerHandler(eventClass, eventHandler, listenOrder);
		}
	}

	@Override
	public <E extends Event> void unregisterHandler(Class<E> eventClass,
													EventHandler<? super E> eventHandler,
													ListenOrder listenOrder, GlobalPlugin plugin) {
		for (World world : plugin.getActiveWorlds()) {
			world.getEventsManager().unregisterHandler(eventClass, eventHandler, listenOrder);
		}
	}
}