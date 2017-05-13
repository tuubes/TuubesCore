package org.mcphoton.entity.living;

import org.mcphoton.entity.Entity;
import org.mcphoton.entity.HeadRotateable;

/**
 * A living entity.
 *
 * @author TheElectronWill
 */
public interface LivingEntity extends Entity, HeadRotateable {

	/**
	 * @return true if this entity is invulnerable.
	 */
	boolean isInvulnerable();

	/**
	 * Sets if this entity is invulnerable.
	 *
	 * @param invulnerable true if it's invulnerable.
	 */
	void setInvulnerable(boolean invulnerable);

	/**
	 * @return the entity's health.
	 */
	float getHealth();

	/**
	 * Sets the entity's health.
	 *
	 * @param health the health to set.
	 */
	void setHealth(float health);

	/**
	 * @return the entity's maximum health.
	 */
	float getMaxHealth();

	/**
	 * Damages this entity.
	 *
	 * @param damage the value to remove from the entity's health.
	 */
	void damage(float damage);

	/**
	 * Kills the entity.
	 */
	void kill();

	/**
	 * @return true if the entity is alive.
	 */
	default boolean isAlive() {
		return getHealth() > 0;
	}

	/**
	 * @return the entity's remaining air level.
	 */
	int getAirLevel();

	/**
	 * Sets the entity's air level.
	 *
	 * @param level the remaining air level to set.
	 */
	void setAirLevel(int level);

	/**
	 * @return the entity's maximum air level.
	 */
	int getMaxAirLevel();

	/**
	 * @return the number of arrows stuck in the entity.
	 */
	int getStuckArrowsNumber();

	/**
	 * Sets the number of arrows stuck in the entity.
	 *
	 * @param stuckArrowsNumber the number of arrows stuck in the entity, so that the game client can display them.
	 */
	void setStuckArrowsNumber(int stuckArrowsNumber);

}
