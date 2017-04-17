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
package org.mcphoton.entity;

import java.util.Optional;
import java.util.UUID;
import org.mcphoton.entity.vehicle.Vehicle;
import org.mcphoton.network.Packet;
import org.mcphoton.utils.DoubleVector;
import org.mcphoton.utils.Location;
import org.mcphoton.world.World;

/**
 * Base interface for entites.
 * <h2>About thread-safety</h2>
 * Thanks to the new photon thread model, the entities generally don't need to be thread-safe.
 *
 * @author TheElectronWill
 * @author DJmaxZPLAY
 */
public interface Entity {

	/**
	 * @return the entity's id.
	 */
	int getEntityId();

	/**
	 * @return the entity's UUID.
	 */
	UUID getUniqueId();

	/**
	 * Initializes the entity. This method may only be called once.
	 */
	void init(int entityId, double x, double y, double z, World w);

	/**
	 * @return true if the entity is valid, ie if it has been initialized but not invalidated yet.
	 */
	boolean isValid();

	/**
	 * Invalidates the entity.
	 */
	void invalidate();

	/**
	 * @return the entity's type.
	 */
	EntityType getType();

	/**
	 * @return the entity's custom name.
	 */
	String getCustomName();

	/**
	 * Sets the entity's custom name.
	 */
	void setCustomName(String customName);

	/**
	 * @return true if custom name is visible
	 */
	boolean isCustomNameVisible();

	/**
	 * Sets if the custom name of the entity is visible.
	 *
	 * @param visibility true if custom name must be shown.
	 */
	void setCustomNameVisible(boolean visibility);

	/**
	 * @return the velocity (speed) per ticks of the entity.
	 */
	DoubleVector getVelocity();

	/**
	 * Sets the entity's velocity. Please consider using {@link #getVelocity()} and modifying the DoubleVector
	 * instead of using this method.
	 *
	 * @param v the new entity velocity.
	 */
	void setVelocity(DoubleVector v);

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
	 * @return true if the entity is on the ground.
	 */
	boolean isOnGround();

	/**
	 * Sets if the entity is on the ground.
	 *
	 * @param onGround true if the entity on the ground.
	 */
	void setOnGround(boolean onGround);

	/**
	 * @return true if the entity is on fire.
	 */
	boolean isOnFire();

	/**
	 * Sets if the entity is on fire.
	 *
	 * @param onFire true if the entity is on fire. Setting this to false will also sets the "fire ticks"
	 * counter to 0.
	 */
	void setOnFire(boolean onFire);

	/**
	 * @return the number of ticks this entity will stay in fire. 0 if it's not in fire.
	 */
	int getFireTicks();

	/**
	 * Sets the number of ticks this entity will stay in fire.
	 *
	 * @param ticks number of ticks this entity will stay in fire. 0 to stop the fire.
	 */
	void setFireTicks(int ticks);

	/**
	 * @return true if this entity is crouched.
	 */
	boolean isCrouched();

	/**
	 * Sets if the entity is crouched.
	 *
	 * @param crouched true if it's crouched.
	 */
	void setCrouched(boolean crouched);

	/**
	 * @return true if the entity is sprinting.
	 */
	boolean isSprinting();

	/**
	 * Sets if the entity is sprinting.
	 *
	 * @param sprinting true if it's sprinting.
	 */
	void setSprinting(boolean sprinting);

	/**
	 * @return true if the entity is subject to gravity.
	 */
	boolean hasGravity();

	/**
	 * Sets if the entity is subject to gravity.
	 *
	 * @param gravity true if the gravity should be applied.
	 */
	void setGravity(boolean gravity);

	/**
	 * @return true if the entity is glowing.
	 */
	boolean isGlowing();

	/**
	 * Sets if the entity glows.
	 *
	 * @param glow true if the entity glows.
	 */
	void setGlowing(boolean glow);

	/**
	 * @return true if the entity is silent.
	 */
	boolean isSilent();

	/**
	 * Sets if the entity is silent.
	 *
	 * @param silent true if the entity is silent.
	 */
	void setSilent(boolean silent);

	/**
	 * @return the vehicle this entity is in.
	 */
	Optional<Vehicle> getVehicle();

	/**
	 * Makes the entity leave its vehicle. If the entity isn't in a vehicle then this method has no effect.
	 */
	void leaveVehicle();

	/**
	 * Writes the entity's metadata to a MetadataWriter.
	 */
	void writeMetadata(MetadataWriter writer);

	/**
	 * Constructs a packet that can be sent to a client to spawn the entity.
	 *
	 * @return a spawn packet.
	 */
	Packet constructSpawnPacket();

}
