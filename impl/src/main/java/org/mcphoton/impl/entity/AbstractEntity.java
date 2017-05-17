package org.mcphoton.impl.entity;

import org.mcphoton.entity.Entity;
import org.mcphoton.impl.world.WorldImpl;
import org.mcphoton.utils.Location;
import org.mcphoton.utils.Vector;

/**
 * @author TheElectronWill
 */
public abstract class AbstractEntity implements Entity {
	protected WorldImpl world;
	protected Vector position, velocity;
	protected int id = -1;

	protected AbstractEntity(Location location) {
		position = location.toVector();
		world = (WorldImpl)location.getWorld();
		velocity = new Vector();
	}

	public void init(int id) {
		if (this.id != -1) {
			throw new IllegalStateException("Entity already initialized with id " + id);
		}
		this.id = id;
	}

	@Override
	public Vector getVelocity() {
		return velocity;
	}

	@Override
	public void setVelocity(Vector v) {
		velocity = v;
	}

	@Override
	public Location getLocation() {
		return new Location(position, world);
	}

	@Override
	public boolean teleport(Location location) {
		world.removeEntity(id);
		//TODO send teleport packet?
		position = location.toVector();
		world = (WorldImpl)location.getWorld();
		world.addEntity(this);
		return true;
		//TODO return false in case of error
	}

	@Override
	public void spawn() {
		world.addEntity(this);
		//TODO send the spawn packet
	}

	@Override
	public void destroy() {
		world.removeEntity(id);
		//TODO send the despawn packet
		id = -1;
	}

	@Override
	public boolean exists() {
		return id >= 0;
	}
}