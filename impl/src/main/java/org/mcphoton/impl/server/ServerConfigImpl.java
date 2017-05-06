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
import org.mcphoton.server.LogLevel;
import org.mcphoton.server.ServerConfiguration;
import org.mcphoton.utils.Location;
import org.mcphoton.world.World;

/**
 * The server configuration, loaded from the file server-config.toml with the Night-Config library.
 *
 * @author TheElectronWill
 */
public final class ServerConfigImpl implements ServerConfiguration {
	private static final File configFile = new File(Photon.getMainDirectory(),
													"server-config.toml");

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

	static ServerConfiguration load() {
		TomlConfig config = new TomlParser().parse(configFile);
		ServerConfigImpl impl = new ObjectConverter().toObject(config, ServerConfigImpl::new);
		impl.savedComments = config.getComments();// Remembers the comments
		impl.icon = readIcon();
		return impl;
	}

	void save() {
		TomlConfig config = new ObjectConverter().toConfig(this, TomlConfig::new);
		config.setComments(savedComments);// Restores the comments
		config.write(configFile);
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
			String world = parts.get(3);
			World w = Photon.getServer().getWorld(world);
			return new Location(x, y, z, w);
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