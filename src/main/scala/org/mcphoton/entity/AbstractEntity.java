package org.mcphoton.entity;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityDestroyPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityTeleportPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityVelocityPacket;
import com.github.steveice10.packetlib.packet.Packet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.mcphoton.entity.mobs.PlayerImpl;
import org.mcphoton.impl.runtime.ExecutionGroup;
import org.mcphoton.impl.runtime.Updatable;
import org.mcphoton.world.PlayerZone;
import org.mcphoton.world.WorldImpl;
import org.mcphoton.world.Location;
import org.mcphoton.utils.Vector;

/**
 * Base type for entities (mobs, "objects", XP orbs, etc.)
 *
 * @author TheElectronWill
 */
public abstract class AbstractEntity implements Entity, Updatable {
	protected volatile WorldImpl world;
	public volatile Vector position, velocity;
	protected volatile int id = -1;
	protected volatile ExecutionGroup executionGroup;
	protected final Set<PlayerZone> playerZones = ConcurrentHashMap.newKeySet();

	/** Delta between the new and the previous position. Used in sendChanges() */
	protected Vector positionChange;
	/** True if the velocity has changed since the last sendUpdates() */
	protected boolean velocityChanged;

	protected AbstractEntity(Location location) {
		position = location.toVector();
		world = (WorldImpl)location.getWorld();
		velocity = new Vector();
	}

	public void init(int id, ExecutionGroup executionGroup) {
		if (this.id != -1) {
			throw new IllegalStateException("Entity already initialized with id " + id);
		}
		this.id = id;
		this.executionGroup = executionGroup;
	}

	@Override
	public ExecutionGroup getAssociatedContext() {
		return executionGroup;
	}

	@Override
	public Vector getVelocity() {
		return velocity.clone();
	}

	@Override
	public void setVelocity(Vector v) {
		velocity = v.clone();
	}

	@Override
	public Location getLocation() {
		return new Location(position, world);
	}

	@Override
	public boolean teleport(Location location) {
		position = location.toVector();
		if (location.getWorld() == world) {
			sendToClients(new ServerEntityTeleportPacket(id, position, 0, 0, true));
		} else {
			sendToClients(new ServerEntityDestroyPacket(id));
			world.getEntitiesManager().unregisterEntity(id);
			world = (WorldImpl)location.getWorld();
			world.getEntitiesManager().registerEntity(this);
		}
		return true;
	}

	@Override
	public void spawn() {
		world.getEntitiesManager().registerEntity(this);
	}

	@Override
	public void destroy() {
		world.getEntitiesManager().unregisterEntity(id);
		//TODO send the despawn packet
		id = -1;
	}

	@Override
	public boolean exists() {
		return id >= 0;
	}

	@Override
	public void update(double dt) {
		position.add(velocity);
		positionChange = velocity;
	}

	@Override
	public void sendUpdates() throws IOException {
		List<Packet> packets = createUpdatePackets();
		sendToClients(packets);
	}

	protected List<Packet> createUpdatePackets() {
		List<Packet> packets = new ArrayList<>(4);
		if (velocityChanged) {
			packets.add(new ServerEntityVelocityPacket(id, velocity));
		}
		return packets;
	}

	protected void sendToClients(Packet packet) {
		sendToClients(Collections.singletonList(packet));
	}

	protected void sendToClients(List<Packet> packets) {
		for (Iterator<PlayerZone> iterator = playerZones.iterator(); iterator.hasNext(); ) {
			PlayerZone zone = iterator.next();
			PlayerImpl player = zone.getPlayer();
			if (player.exists()) {
				for (Packet packet : packets) {
					zone.getPlayer().getSession().send(packet);
				}
			} else {
				iterator.remove();
			}
		}
	}
}