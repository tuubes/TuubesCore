package org.mcphoton.impl.event;

import com.electronwill.utils.Bag;
import com.electronwill.utils.SimpleBag;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.mcphoton.event.CancellableEvent;
import org.mcphoton.event.Event;
import org.mcphoton.event.EventHandler;
import org.mcphoton.event.Listen;
import org.mcphoton.event.ListenOrder;
import org.mcphoton.event.WorldEventsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link WorldEventsManager}. This class is thread-safe because all the operations are
 * guarded by synchronized blocks. In particular, only one event can be posted at a time, therefore the event
 * handlers don't need to be thread-safe.
 *
 * @author TheElectronWill
 */
public class WorldEventsManagerImpl implements WorldEventsManager {

	private static final Logger log = LoggerFactory.getLogger(WorldEventsManagerImpl.class);
	private final Map<Class<? extends Event>, EnumMap<ListenOrder, Bag<EventHandler>>> handlersMap = new HashMap<>();

	@Override
	public <E extends Event> void registerHandler(Class<E> eventClass, EventHandler<? super E>
			eventHandler, ListenOrder listenOrder) {
		synchronized (handlersMap) {
			EnumMap<ListenOrder, Bag<EventHandler>> orderMap = handlersMap.get(eventClass);
			if (orderMap == null) {
				orderMap = new EnumMap(ListenOrder.class);
				handlersMap.put(eventClass, orderMap);
			}
			Bag<EventHandler> handlers = orderMap.get(listenOrder);
			if (handlers == null) {
				handlers = new SimpleBag();
				orderMap.put(listenOrder, handlers);
			}
			handlers.add(eventHandler);
		}
	}

	@Override
	public <E extends Event> void unregisterHandler(Class<E> eventClass, EventHandler<? super E>
			eventHandler, ListenOrder listenOrder) {
		synchronized (handlersMap) {
			EnumMap<ListenOrder, Bag<EventHandler>> orderMap = handlersMap.get(eventClass);
			if (orderMap == null) {
				return;
			}
			Bag<EventHandler> handlers = orderMap.get(listenOrder);
			if (handlers == null) {
				return;
			}
			handlers.remove(eventHandler);
		}
	}

	@Override
	public void registerHandlers(Object listener) {
		Method[] publicMethods = listener.getClass().getMethods();
		for (Method method : publicMethods) {
			//--- Checks if there is a @Listen annotation ---
			Listen listenAnnotation = method.getAnnotation(Listen.class);
			if (listenAnnotation == null) {
				continue;
			}

			//--- Checks the parameter count ---
			int pCount = method.getParameterCount();
			if (pCount != 1) {
				throw new IllegalArgumentException("Method " + method.toGenericString() + " must have 1 parameter, but it has " + pCount + ".");
			}

			//--- Checks the parameter type ---
			Class<?> pClass = method.getParameterTypes()[0];
			if (!Event.class.isAssignableFrom(pClass)) {
				throw new IllegalArgumentException("Method " + method.toGenericString() + " must take an Event as parameter.");
			}

			// --- Constructs and registers the event handler ---
			ListenOrder order = listenAnnotation.order();
			boolean ignoreCancelled = listenAnnotation.ignoreCancelled();

			if (ignoreCancelled && CancellableEvent.class.isAssignableFrom(pClass)) {//ignore any cancelled event
				Class<? extends CancellableEvent> eventClass = (Class<? extends CancellableEvent>) pClass;
				EventHandler<CancellableEvent> handler = new IgnoreCancelledReflectionEventHandler<>(method);
				registerHandler(eventClass, handler, order);
			} else {//don't care about the cancelled state of the event
				Class<? extends Event> eventClass = (Class<? extends Event>) pClass;
				EventHandler<Event> handler = new ReflectionEventHandler<>(method);
				registerHandler(eventClass, handler, order);
			}
		}
	}

	@Override
	public void unregisterHandlers(Object listener) {
		Method[] publicMethods = listener.getClass().getMethods();
		for (Method method : publicMethods) {
			//--- Checks if there is a @Listen annotation ---
			Listen listenAnnotation = method.getAnnotation(Listen.class);
			if (listenAnnotation == null) {
				continue;
			}

			//--- Checks the parameter count ---
			int pCount = method.getParameterCount();
			if (pCount != 1) {
				throw new IllegalArgumentException("Method " + method.toGenericString() + " must have 1 parameter, but it has " + pCount + ".");
			}

			//--- Checks the parameter type ---
			Class<?> pClass = method.getParameterTypes()[0];
			if (!Event.class.isAssignableFrom(pClass)) {
				throw new IllegalArgumentException("Method " + method.toGenericString() + " must take an Event as parameter.");
			}

			//--- Unregisters the event handler ---
			ListenOrder order = listenAnnotation.order();
			synchronized (handlersMap) {
				EnumMap<ListenOrder, Bag<EventHandler>> orderMap = handlersMap.get(pClass);
				if (orderMap == null) {//nothing to unregister for this event type
					return;
				}
				Bag<EventHandler> handlers = orderMap.get(order);//the handlers of this event type
				Iterator<EventHandler> it = handlers.iterator();
				while (it.hasNext()) {
					EventHandler handler = it.next();
					if (handler instanceof ReflectionEventHandler && ((ReflectionEventHandler) handler).handlerMethod.equals(method)) {
						//If the handler executes the same method as the one in the listener, remove the handler and return.
						it.remove();
						return;
					}
				}
			}
		}
	}

	@Override
	public void post(Event event) {
		synchronized (handlersMap) {
			EnumMap<ListenOrder, Bag<EventHandler>> orderMap = handlersMap.get(event.getClass());
			if (orderMap == null) {
				return;
			}
			for (Bag<EventHandler> handlers : orderMap.values()) {
				if (handlers != null) {
					for (EventHandler handler : handlers) {
						handler.handle(event);
					}
				}
			}
		}
	}

	/**
	 * An event handler that invokes a {@link Method}.
	 */
	private static class ReflectionEventHandler<E extends Event> implements EventHandler<E> {

		protected final Method handlerMethod;

		public ReflectionEventHandler(Method handlerMethod) {
			this.handlerMethod = handlerMethod;
		}

		@Override
		public void handle(E event) {
			try {
				handlerMethod.invoke(event);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
				log.error("An unexpected error occured in an EventHandler.", ex);
			}
		}
	}

	/**
	 * An event handler that invokes a {@link Method} if the event isn't cancelled (ie cancelled events are
	 * ignored).
	 */
	private static class IgnoreCancelledReflectionEventHandler<E extends CancellableEvent> extends ReflectionEventHandler<E> {

		public IgnoreCancelledReflectionEventHandler(Method handlerMethod) {
			super(handlerMethod);
		}

		@Override
		public void handle(CancellableEvent event) {
			if (!event.isCancelled()) {
				try {
					handlerMethod.invoke(event);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
					log.error("An unexpected error occured in an EventHandler.", ex);
				}
			}
		}
	}
}