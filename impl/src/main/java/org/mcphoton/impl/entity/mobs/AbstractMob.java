package org.mcphoton.impl.entity.mobs;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnMobPacket;
import java.util.UUID;
import org.mcphoton.impl.entity.AbstractEntity;
import org.mcphoton.utils.Location;

/**
 * @author TheElectronWill
 */
public abstract class AbstractMob extends AbstractEntity {
	protected AbstractMob(Location location) {
		super(location);
	}

	@Override
	public abstract AbstractMobType getType();

	@Override
	public void spawn() {
		super.spawn();
		UUID uuid = new UUID(0, id);
		int typeId = getType().getId();
		//TODO new ServerSpawnMobPacket(id, uuid, typeId,position,yaw,pitch,headYaw,velocity,metadata);
	}
}
