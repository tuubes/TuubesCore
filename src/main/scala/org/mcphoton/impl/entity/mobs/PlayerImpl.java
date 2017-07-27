package org.mcphoton.impl.entity.mobs;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.MetadataType;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.TrackedMetadataValue;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.packet.Packet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.mcphoton.entity.living.Player;
import org.mcphoton.impl.world.PlayerZone;
import org.mcphoton.inventory.Inventory;
import org.mcphoton.messaging.ChatMessage;
import org.mcphoton.utils.Location;

/**
 * Represents a connected player.
 *
 * @author TheElectronWill
 */
public class PlayerImpl extends AbstractMob implements Player {
	private final Session session;
	/**
	 * The zone around the player that is used to notify it about the world updates. Note that
	 * the PlayerImpl is not in its own {@link #playerZones} Set, therefore the methods
	 * {@link #sendToClients(Packet)} don't send the packet to the PlayerImpl, only to the other
	 * players.
	 */
	private final PlayerZone zone;
	private final UUID accoundId;
	private final String name;

	public PlayerImpl(Location location, String name, UUID accoundId, Session session,
					  int zoneSize) {
		super(location);
		this.name = name;
		this.accoundId = accoundId;
		this.session = session;
		this.zone = new PlayerZone(this, zoneSize);
	}

	public Session getSession() {
		return session;
	}

	public PlayerZone getZone() {
		return zone;
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
		world.getEntitiesManager().registerEntity(this);
		Packet packet = new ServerSpawnPlayerPacket(id, accoundId, position, yaw, pitch,
													dataStorage);
		sendToClients(packet);
		//TODO send the packet when a new player, that isn't aware of the existence of this player,
		// gets in the zone
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