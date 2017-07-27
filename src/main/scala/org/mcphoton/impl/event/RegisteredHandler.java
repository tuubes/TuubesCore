package org.mcphoton.impl.event;

import java.util.Comparator;
import org.mcphoton.event.Event;
import org.mcphoton.event.EventHandler;

/**
 * @author TheElectronWill
 */
public final class RegisteredHandler<E extends Event> implements EventHandler<E> {
	final EventHandler<E> handler;
	final int order;

	public RegisteredHandler(EventHandler<E> handler, int order) {
		this.handler = handler;
		this.order = order;
	}

	@Override
	public void handle(E event) {
		handler.handle(event);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) { return true; }
		if (!(obj instanceof RegisteredHandler)) { return false; }
		RegisteredHandler other = (RegisteredHandler)obj;
		return order == other.order && handler.equals(other.handler);
	}

	static final class OrderComparator implements Comparator<RegisteredHandler> {

		@Override
		public int compare(RegisteredHandler o1, RegisteredHandler o2) {
			return o1.order - o2.order;
		}
	}
}