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
import org.mcphoton.utils.Vector;
import org.mcphoton.utils.Location;
import org.mcphoton.utils.MutableLocation;
import org.mcphoton.world.World;

/**
 * Abstract base class for entities.
 *
 * @author TheElectronWill
 */
public abstract class AbstractEntity implements Entity {

	private int entityId = -1;
	private UUID uniqueId;

	private String customName;
	private boolean customNameVisible;

	private int fireTicks;
	private boolean onGround, onFire, crouched, glowing, gravity, silent, sprinting;
	private Optional<Vehicle> vehicle = Optional.empty();

	private MutableLocation loc;
	private Vector velocity = new Vector();

	@Override
	public String getCustomName() {
		return customName;
	}

	@Override
	public void setCustomName(String customName) {
		this.customName = customName;
	}

	@Override
	public boolean isCustomNameVisible() {
		return customNameVisible;
	}

	@Override
	public void setCustomNameVisible(boolean visibility) {
		this.customNameVisible = visibility;
	}

	@Override
	public int getEntityId() {
		return entityId;
	}

	@Override
	public int getFireTicks() {
		return fireTicks;
	}

	@Override
	public void setFireTicks(int ticks) {
		this.fireTicks = ticks;
	}

	@Override
	public void setGravity(boolean gravity) {
		this.gravity = gravity;
	}

	@Override
	public Location getLocation() {
		return loc;
	}

	@Override
	public UUID getUniqueId() {
		return uniqueId;
	}

	@Override
	public Optional<Vehicle> getVehicle() {
		return vehicle;
	}

	@Override
	public Vector getVelocity() {
		return velocity;
	}

	@Override
	public void setVelocity(Vector v) {
		this.velocity = v;
	}

	@Override
	public boolean hasGravity() {
		return gravity;
	}

	@Override
	public void init(int entityId, double x, double y, double z, World w) {
		if (this.entityId != -1) {
			throw new IllegalStateException("Entity id already initialized!");
		}
		this.entityId = entityId;
		this.uniqueId = new UUID(0, entityId);
		this.loc = new MutableLocation(x, y, z, w);
	}

	@Override
	public boolean isValid() {
		return entityId >= 0;
	}

	@Override
	public void invalidate() {
		entityId = -1;
	}

	@Override
	public boolean isCrouched() {
		return crouched;
	}

	@Override
	public void setCrouched(boolean crouched) {
		this.crouched = crouched;
	}

	@Override
	public boolean isGlowing() {
		return glowing;
	}

	@Override
	public void setGlowing(boolean glow) {
		this.glowing = glow;
	}

	@Override
	public boolean isOnFire() {
		return onFire;
	}

	@Override
	public void setOnFire(boolean onFire) {
		this.onFire = onFire;
	}

	@Override
	public boolean isOnGround() {
		return onGround;
	}

	@Override
	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}

	@Override
	public boolean isSilent() {
		return silent;
	}

	@Override
	public void setSilent(boolean silent) {
		this.silent = silent;
	}

	@Override
	public boolean isSprinting() {
		return sprinting;
	}

	@Override
	public void setSprinting(boolean sprinting) {
		this.sprinting = sprinting;
	}
}