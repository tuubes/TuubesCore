package org.mcphoton.world;

import org.mcphoton.entity.mobs.PlayerImpl;
import org.mcphoton.utils.Coordinates;
import org.mcphoton.utils.Vector;

/**
 * A zone around a player. Every change in the zone is sent to the client.
 *
 * @author TheElectronWill
 */
public final class PlayerZone {
	private final PlayerImpl player;
	/** XZ size */
	private final int size;

	public PlayerZone(PlayerImpl player, int size) {
		this.player = player;
		this.size = size;
	}

	public boolean contains(double x, double z) {
		Vector pos = player.position;
		return pos.getX() - size <= x && x <= pos.getX() + size
			   && pos.getZ() - size <= z && z <= pos.getZ() + size;
	}

	public boolean contains(Coordinates coords) {
		return contains(coords.getX(), coords.getZ());
	}

	public PlayerImpl getPlayer() {
		return player;
	}
}