package org.mcphoton.entity;

import org.mcphoton.runtime.ContextBound;
import org.mcphoton.world.Location;
import org.mcphoton.utils.Vector;

/**
 * Base interface for entites.
 *
 * @author TheElectronWill
 * @author DJmaxZPLAY
 */
public interface Entity extends ContextBound {
	/**
	 * @return the entity's type.
	 */
	EntityType getType();

	/**
	 * @return the velocity (speed) per ticks of the entity.
	 */
	Vector getVelocity();

	/**
	 * Sets the entity's velocity.
	 *
	 * @param v the new entity velocity.
	 */
	void setVelocity(Vector v);

	/**
	 * @return the current entity's location.
	 */
	Location getLocation();

	/**
	 * Teleports the entity.
	 *
	 * @param location the location to teleport the entity to.
	 * @return true if the teleportation succeeds.
	 */
	boolean teleport(Location location);

	/**
	 * Makes the entity appear in its world.
	 */
	void spawn();

	/**
	 * Destroys this entity. Removes it from its world and frees its associated ressources.
	 */
	void destroy();

	/**
	 * @return {@code true} if this entity exists in the world, {@code false} if hasn't spawned
	 * yet or if has been destroyed.
	 */
	boolean exists();
}