package org.mcphoton.impl.entity.mobs;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
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