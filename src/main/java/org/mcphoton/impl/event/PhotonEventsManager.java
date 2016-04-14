package org.mcphoton.impl.event;

import com.electronwill.utils.Bag;
import com.electronwill.utils.SimpleBag;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import org.mcphoton.event.CancellableEvent;
import org.mcphoton.event.Event;
import org.mcphoton.event.EventHandler;
import org.mcphoton.event.EventsManager;
import org.mcphoton.event.Listen;
import org.mcphoton.event.ListenOrder;
import org.mcphoton.impl.Main;

/**
 *
 * @author TheElectronWill
 */
public class PhotonEventsManager implements EventsManager {

	private final Map<Class<? extends Event>, EnumMap<ListenOrder, Bag<EventHandler>>> handlersMap = new HashMap<>();

	@Override
	public void registerAll(Object listener) {
		Method[] publicMethods = listener.getClass().getMethods();
		for (Method method : publicMethods) {

			Listen listenAnnotation = method.getAnnotation(Listen.class);
			if (listenAnnotation == null) {
				continue;
			}

			int pCount = method.getParameterCount();
			if (pCount != 1) {
				throw new IllegalArgumentException("Method " + method.toGenericString() + " must have 1 parameter, but it has " + pCount + " parameters");
			}

			Class<?> pClass = method.getParameterTypes()[0];
			if (!Event.class.isAssignableFrom(pClass)) {
				throw new IllegalArgumentException("Method " + method.toGenericString() + " must take a PhotonEvent as parameter");
			}

			ListenOrder order = listenAnnotation.order();
			boolean ignoreCancelled = listenAnnotation.ignoreCancelled();
			if (ignoreCancelled && CancellableEvent.class.isAssignableFrom(pClass)) {
				Class<? extends CancellableEvent> eventClass = (Class<? extends CancellableEvent>) pClass;
				EventHandler<CancellableEvent> handler = (CancellableEvent e) -> {
					try {
						if (!e.isCancelled()) {
							method.invoke(listener, e);
						}
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
						Main.serverInstance.logger.error("An error occured in an EventHandler", ex);
					}
				};
				register(eventClass, handler, order);
			} else {
				Class<? extends Event> eventClass = (Class<? extends Event>) pClass;
				EventHandler<Event> handler = (Event e) -> {
					try {
						method.invoke(listener, e);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
						Main.serverInstance.logger.error("An error occured in an EventHandler", ex);
					}
				};
				register(eventClass, handler, order);
			}
		}
	}

	@Override
	public <E extends Event> void register(Class<E> eventClass, EventHandler<? super E> eventHandler, ListenOrder listenOrder) {
		EnumMap<ListenOrder, Bag<EventHandler>> orderMap;
		synchronized (handlersMap) {
			orderMap = handlersMap.get(eventClass);
			if (orderMap == null) {
				orderMap = new EnumMap(ListenOrder.class);
				handlersMap.put(eventClass, orderMap);
			}
		}
		synchronized (orderMap) {
			Bag<EventHandler> handlers = orderMap.get(listenOrder);
			if (handlers == null) {
				handlers = new SimpleBag();
				orderMap.put(listenOrder, handlers);
			}
			handlers.add(eventHandler);
		}
	}

	@Override
	public <E extends Event> void unregister(Class<E> eventClass, EventHandler<? super E> eventHandler, ListenOrder listenOrder) {
		final EnumMap<ListenOrder, Bag<EventHandler>> orderMap;
		synchronized (handlersMap) {
			orderMap = handlersMap.get(eventClass);
			if (orderMap == null) {
				return;
			}
		}
		synchronized (orderMap) {
			Bag<EventHandler> handlers = orderMap.get(listenOrder);
			if (handlers == null) {
				return;
			}
			handlers.remove(eventHandler);
		}
	}

	@Override
	public <E extends Event> void unregister(Class<E> eventClass, EventHandler<? super E> eventHandler) {
		//TODO REMOVE FROM API: this method is useless
	}

	@Override
	public void unregisterAll(Object listener) {
		; //TODO REMOVE FROM API: impossible to implement. How to know precisely the EventHandlers registered by this listener???
	}

	@Override
	public void post(Event event) {
		final EnumMap<ListenOrder, Bag<EventHandler>> orderMap;
		synchronized (handlersMap) {
			orderMap = handlersMap.get(event.getClass());
			if (orderMap == null) {
				return;
			}
		}
		synchronized (orderMap) {
			for (Bag<EventHandler> handlers : orderMap.values()) {
				if (handlers != null) {
					for (EventHandler handler : handlers) {
						handler.handle(event);
					}
				}
			}
		}
	}

}
