package org.mcphoton.impl.world;

import java.util.Optional;
import org.mcphoton.world.ChunkColumn;
import org.mcphoton.world.World;
import org.mcphoton.world.areas.Area;
import org.mcphoton.world.protection.CheckedWorldAccess;
import org.mcphoton.world.protection.UnlockedAreaAccess;
import org.mcphoton.world.protection.UnlockedWorldAccess;
import org.mcphoton.world.protection.WorldAccessManager;

/**
 *
 * @author TheElectronWill
 */
public final class OpenWorldAccessManager implements WorldAccessManager {

	private final World world;

	public OpenWorldAccessManager(World world) {
		this.world = world;
	}

	@Override
	public CheckedWorldAccess getAccess() {
		throw new UnsupportedOperationException();//TODO
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public Optional<UnlockedAreaAccess> unlockArea(Area area, Object accessor) {
		throw new UnsupportedOperationException();//TODO
	}

	@Override
	public Optional<ChunkColumn> unlockChunk(int x, int z, Object accessor) {
		throw new UnsupportedOperationException();//TODO
	}

	@Override
	public Optional<UnlockedWorldAccess> unlockWorld(Object accessor) {
		throw new UnsupportedOperationException();//TODO
	}

}
