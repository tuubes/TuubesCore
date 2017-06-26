package org.mcphoton.impl.event;

import com.electronwill.utils.ConcurrentSortedCollection;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import org.mcphoton.event.CancellableEvent;
import org.mcphoton.event.Event;
import org.mcphoton.event.EventHandler;
import org.mcphoton.event.Listen;
import org.mcphoton.event.ListenOrder;
import org.mcphoton.event.WorldEventsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link WorldEventsManager}. This class is thread-safe.
 * <p>
 * Note that multiple events may be posted at the same time, and the same EventHandler may be
 * called from different threads. Therefore the EventHandlers must be thread-safe.
 *
 * @author TheElectronWill
 */
public final class WorldEventsManagerImpl implements WorldEventsManager {

	private static final Logger log = LoggerFactory.getLogger(WorldEventsManagerImpl.class);
	private final Comparator<RegisteredHandler> handlersComparator = new RegisteredHandler.OrderComparator();
	private final Function<Class, Collection<RegisteredHandler>> collectionBuilder = key -> new ConcurrentSortedCollection<>(
			handlersComparator);
	private final ConcurrentMap<Class<? extends Event>, Collection<RegisteredHandler>> handlersMap = new ConcurrentHashMap<>();

	@Override
	public <E extends Event> void registerHandler(Class<E> eventClass,
												  EventHandler<? super E> eventHandler,
												  ListenOrder listenOrder) {
		RegisteredHandler<? super E> handler = new RegisteredHandler<>(eventHandler, listenOrder.ordinal());
		Collection<RegisteredHandler> handlers = handlersMap.computeIfAbsent(eventClass, collectionBuilder);
		handlers.add(handler);
	}

	@Override
	public <E extends Event> void unregisterHandler(Class<E> eventClass,
													EventHandler<? super E> eventHandler,
													ListenOrder listenOrder) {
		RegisteredHandler<? super E> handler = new RegisteredHandler<>(eventHandler, listenOrder.ordinal());
		Collection<RegisteredHandler> handlers = handlersMap.computeIfAbsent(eventClass, collectionBuilder);
		handlers.remove(handler);
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
				throw new IllegalArgumentException("Method "
												   + method.toGenericString()
												   + " must have 1 parameter, but it has "
												   + pCount
												   + ".");
			}

			//--- Checks the parameter type ---
			Class<?> pClass = method.getParameterTypes()[0];
			if (!Event.class.isAssignableFrom(pClass)) {
				throw new IllegalArgumentException(
						"Method " + method.toGenericString() + " must take an Event as parameter.");
			}

			// --- Constructs and registers the event handler ---
			ListenOrder order = listenAnnotation.order();
			boolean ignoreCancelled = listenAnnotation.ignoreCancelled();
			if (ignoreCancelled && CancellableEvent.class.isAssignableFrom(
					pClass)) {//ignore any cancelled event
				Class<? extends CancellableEvent> eventClass = (Class<? extends CancellableEvent>)pClass;
				EventHandler<CancellableEvent> handler = new IgnoreCancelledReflectionEventHandler<>(method);
				registerHandler(eventClass, handler, order);
			} else {//don't care about the cancelled state of the event
				Class<? extends Event> eventClass = (Class<? extends Event>)pClass;
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
				throw new IllegalArgumentException("Method "
												   + method.toGenericString()
												   + " must have 1 parameter, but it has "
												   + pCount
												   + ".");
			}

			//--- Checks the parameter type ---
			Class<?> pClass = method.getParameterTypes()[0];
			if (!Event.class.isAssignableFrom(pClass)) {
				throw new IllegalArgumentException(
						"Method " + method.toGenericString() + " must take an Event as parameter.");
			}

			//--- Unregisters the event handler ---
			ListenOrder order = listenAnnotation.order();
			Collection<RegisteredHandler> handlers = handlersMap.get(pClass);
			if (handlers == null) {//nothing to unregister for this event type
				return;
			}
			//ConcurrentSortedCollection's iterator is thread-safe
			Iterator<RegisteredHandler> it = handlers.iterator();
			while (it.hasNext()) {
				RegisteredHandler rHandler = it.next();
				EventHandler eHandler = rHandler.handler;
				if (rHandler.order == order.ordinal()
					&& eHandler instanceof ReflectionEventHandler
					&& ((ReflectionEventHandler)eHandler).handlerMethod.equals(method)) {
					//If the handler executes the same method as the one in the listener, remove the handler and return.
					it.remove();
					return;
				}
			}
		}
	}

	@Override
	public void post(Event event) {
		Collection<RegisteredHandler> handlers = handlersMap.get(event.getClass());
		if (handlers == null) {
			return;
		}
		//ConcurrentSortedCollection's iterator is thread-safe
		for (RegisteredHandler handler : handlers) {
			handler.handle(event);
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
	 * An event handler that invokes a {@link Method} if the event isn't cancelled (ie cancelled
	 * events are
	 * ignored).
	 */
	private static class IgnoreCancelledReflectionEventHandler<E extends CancellableEvent>
			extends ReflectionEventHandler<E> {

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