package org.mcphoton.event;

@FunctionalInterface
public interface EventHandler<E extends Event> {
	void handle(E event);
}