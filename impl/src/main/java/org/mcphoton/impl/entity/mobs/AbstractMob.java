package org.mcphoton.impl.entity.mobs;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.EmptyMetadataStorage;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.MetadataType;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.TrackedMetadataStorage;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.TrackedMetadataValue;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnMobPacket;
import com.github.steveice10.packetlib.packet.Packet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.mcphoton.impl.entity.AbstractEntity;
import org.mcphoton.utils.Location;

/**
 * @author TheElectronWill
 */
public abstract class AbstractMob extends AbstractEntity {
	/** Rotation angle, in radians (converted when writing packets). */
	protected volatile float pitch, yaw, headPitch;
	protected final TrackedMetadataStorage dataStorage = new TrackedMetadataStorage(initializeDataStorage());

	protected AbstractMob(Location location) {
		super(location);
	}

	protected List<TrackedMetadataValue> initializeDataStorage() {
		List<TrackedMetadataValue> values = new ArrayList<>();
		values.add(new TrackedMetadataValue(MetadataType.BYTE, 0));//status byte
		values.add(new TrackedMetadataValue(MetadataType.VARINT, 300));//air level
		values.add(new TrackedMetadataValue(MetadataType.STRING, ""));//custom name
		values.add(new TrackedMetadataValue(MetadataType.BOOLEAN, false));//is custom name visible
		values.add(new TrackedMetadataValue(MetadataType.BOOLEAN, false));//is silent
		values.add(new TrackedMetadataValue(MetadataType.BOOLEAN, false));//true if no gravity
		return values;
	}

	@Override
	public abstract AbstractMobType getType();

	@Override
	public void spawn() {
		super.spawn();
		int id = this.id;
		UUID uuid = new UUID(0, id);
		int typeId = getType().getId();
		Packet packet = new ServerSpawnMobPacket(id, uuid, typeId, position, yaw, pitch, headPitch,
												 velocity, EmptyMetadataStorage.INSTANCE);
	}
}