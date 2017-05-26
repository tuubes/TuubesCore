package org.mcphoton.impl.entity.mobs;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.MetadataType;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.TrackedMetadataValue;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import java.util.List;
import java.util.UUID;
import org.mcphoton.entity.EntityType;
import org.mcphoton.entity.living.Player;
import org.mcphoton.impl.entity.AbstractEntity;
import org.mcphoton.inventory.Inventory;
import org.mcphoton.messaging.ChatMessage;
import org.mcphoton.utils.Location;

/**
 * @author TheElectronWill
 */
public class PlayerImpl extends AbstractMob implements Player {
	private final UUID accoundId;
	private final String name;

	public PlayerImpl(Location location, String name, UUID accoundId) {
		super(location);
		this.name = name;
		this.accoundId = accoundId;
	}

	@Override
	protected List<TrackedMetadataValue> initializeDataStorage() {
		List<TrackedMetadataValue> values = super.initializeDataStorage();
		values.add(new TrackedMetadataValue(MetadataType.FLOAT, 0.0));//additional hearts
		values.add(new TrackedMetadataValue(MetadataType.VARINT, 0));//player score
		values.add(new TrackedMetadataValue(MetadataType.BYTE, 0));//skin settings
		values.add(new TrackedMetadataValue(MetadataType.BYTE, 1));//main hand, 1/0: right/left
		return values;
	}

	@Override
	public void spawn() {
		world.addEntity(this);
		UUID uuid = accoundId;
		int typeId = getType().getId();
		//TODO new ServerSpawnPlayerPacket(...)
	}

	@Override
	public Player asPlayer() {
		return this;
	}

	@Override
	public UUID getAccountId() {
		return accoundId;
	}

	@Override
	public Inventory getInventory() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public void sendMessage(CharSequence msg) {
		throw new UnsupportedOperationException("Not implemented yet.");

	}

	@Override
	public void sendMessage(ChatMessage msg) {
		throw new UnsupportedOperationException("Not implemented yet.");

	}

	@Override
	public boolean hasPermission(String permission, boolean ifNotSet) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public AbstractMobType getType() {
		return null;//TODO StandardMobs.PLAYER
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isPermissionSet(String permission) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public void setLocation(Location l) {
		teleport(l);
	}
}