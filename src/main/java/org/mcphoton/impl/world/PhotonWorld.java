/*
 * Copyright (c) 2016 MCPhoton <http://mcphoton.org> and contributors.
 *
 * This file is part of the Photon Server Implementation <https://github.com/mcphoton/Photon-Server>.
 *
 * The Photon Server Implementation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Photon Server Implementation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mcphoton.impl.world;

import com.electronwill.utils.SimpleBag;
import java.io.File;
import java.util.Collection;
import org.mcphoton.Photon;
import org.mcphoton.entity.living.Player;
import org.mcphoton.world.Location;
import org.mcphoton.world.World;
import org.mcphoton.world.WorldType;
import org.mcphoton.world.protection.WorldAccessManager;

/**
 *
 * @author TheElectronWill
 */
public class PhotonWorld implements World {

	protected volatile String name;
	protected volatile File directory = new File(Photon.WORLD_DIR, name);
	protected volatile double spawnX = 0, spawnY = 0, spawnZ = 0;
	protected final WorldType type;
	protected final Collection<Player> players = new SimpleBag<>();

	public PhotonWorld(String name, WorldType type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public synchronized void renameTo(String name) {
		boolean renameSuccess = directory.renameTo(new File(Photon.WORLD_DIR, name));
		if (renameSuccess) {
			this.name = name;
		}
	}

	@Override
	public File getDirectory() {
		return directory;
	}

	@Override
	public WorldType getType() {
		return type;
	}

	@Override
	public Location getSpawn() {
		return new Location(spawnX, spawnY, spawnZ, this);
	}

	@Override
	public synchronized void setSpawn(int x, int y, int z) {
		this.spawnX = x;
		this.spawnY = y;
		this.spawnZ = z;
	}

	@Override
	public synchronized void setSpawn(Location spawn) {
		this.spawnX = spawn.getX();
		this.spawnY = spawn.getY();
		this.spawnZ = spawn.getZ();
	}

	@Override
	public Collection<Player> getPlayers() {
		return players;
	}

	@Override
	public void save() {
		; //TODO
	}

	@Override
	public void delete() {
		directory.delete();
	}

	@Override
	public WorldAccessManager getAccessManager() {
		return null;//TODO
	}

	@Override
	public void setAccessManager(WorldAccessManager manager) {
		; //TODO
	}

}
