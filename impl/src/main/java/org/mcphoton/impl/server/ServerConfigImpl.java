/*
 * Copyright (c) 2017 MCPhoton <http://mcphoton.org> and contributors.
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
package org.mcphoton.impl.server;

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.electronwill.nightconfig.core.conversion.Conversion;
import com.electronwill.nightconfig.core.conversion.Converter;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.conversion.SpecIntInRange;
import com.electronwill.nightconfig.toml.TomlConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.utils.StringUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.mcphoton.Photon;
import org.mcphoton.impl.world.WorldImpl;
import org.mcphoton.server.LogLevel;
import org.mcphoton.server.ServerConfiguration;
import org.mcphoton.utils.Location;
import org.mcphoton.world.World;
import org.mcphoton.world.WorldType;

/**
 * The server configuration, loaded from the file server-config.toml with the Night-Config library.
 *
 * @author TheElectronWill
 */
public final class ServerConfigImpl implements ServerConfiguration {
	private static final File FILE = new File(Photon.getMainDirectory(), "server-config.toml");

	private volatile String motd = "Default MOTD. Change it in the config.";
	private volatile int maxPlayers = 10;
	private volatile int port = 25565;

	@Conversion(ServerConfigImpl.LocationConverter.class)
	private volatile Location spawnLocation;
	private volatile boolean onlineMode = false;

	@Conversion(ServerConfigImpl.LoggingLevelConverter.class)
	private volatile LogLevel logLevel = LogLevel.TRACE;

	@SpecIntInRange(min = 1, max = 100)
	private volatile int threadNumber;

	private volatile transient BufferedImage icon;// not saved in the config but loaded from icon.jpg or .png
	private volatile transient Map<String, UnmodifiableCommentedConfig.CommentNode> savedComments;

	@Override
	public String getMOTD() {
		return motd;
	}

	@Override
	public void setMOTD(String motd) {
		this.motd = motd;
	}

	@Override
	public int getMaxPlayers() {
		return maxPlayers;
	}

	@Override
	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	@Override
	public int getThreadNumber() {
		return threadNumber;
	}

	@Override
	public void setThreadNumber(int threadNumber) {
		this.threadNumber = threadNumber;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public BufferedImage getIcon() {
		return icon;
	}

	@Override
	public void setIcon(BufferedImage icon) {
		this.icon = icon;
	}

	@Override
	public Location getSpawnLocation() {
		return spawnLocation;
	}

	@Override
	public void setSpawnLocation(Location location) {
		this.spawnLocation = location;
	}

	@Override
	public boolean isOnlineMode() {
		return onlineMode;
	}

	@Override
	public void setOnlineMode(boolean online) {
		this.onlineMode = online;
	}

	@Override
	public LogLevel getLogLevel() {
		return logLevel;
	}

	@Override
	public void setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
	}

	static ServerConfigImpl load() {
		TomlConfig config = new TomlParser().parse(FILE);
		ServerConfigImpl impl = new ObjectConverter().toObject(config, ServerConfigImpl::new);
		impl.savedComments = config.getComments();// Remembers the comments
		impl.icon = readIcon();
		return impl;
	}

	void save() {
		TomlConfig config = new ObjectConverter().toConfig(this, TomlConfig::new);
		config.setComments(savedComments);// Restores the comments
		config.write(FILE);
	}

	private static BufferedImage readIcon() {
		String[] possibilities = {"icon.png", "icon.jpg", "favicon.png", "favicon.jpg",
								  "server-icon.png", "server-icon.jpg", "server_icon.png",
								  "server_icon.jpg", "logo.png", "logo.jpg"};
		for (String possibility : possibilities) {
			File file = new File(Photon.getMainDirectory(), possibility);
			if (file.exists()) {
				try {
					return ImageIO.read(file);
				} catch (IOException e) {
					throw new RuntimeException("Unable to read the logo from " + file, e);
				}
			}
		}
		return null;
	}

	private static class LocationConverter implements Converter<Location, String> {
		@Override
		public Location convertToField(String value) {
			List<String> parts = StringUtils.split(value, ',');
			double x = Double.parseDouble(parts.get(0));
			double y = Double.parseDouble(parts.get(1));
			double z = Double.parseDouble(parts.get(2));
			String worldName = parts.get(3);
			World world = Photon.getServer().getWorld(worldName);
			if(world == null) {
				world = new WorldImpl(worldName, WorldType.OVERWORLD);
			}
			return new Location(x, y, z, world);
		}

		@Override
		public String convertFromField(Location value) {
			return value.getX() + "," + value.getY() + "," + value.getZ() + ", " + value.getWorld()
																						.getName();
		}
	}

	private static class LoggingLevelConverter implements Converter<LogLevel, String> {
		@Override
		public LogLevel convertToField(String value) {
			return LogLevel.valueOf(value.toUpperCase());
		}

		@Override
		public String convertFromField(LogLevel value) {
			return value.name();
		}
	}
}