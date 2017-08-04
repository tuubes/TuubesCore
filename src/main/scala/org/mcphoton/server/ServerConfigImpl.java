package org.mcphoton.server;

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
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.mcphoton.Photon;
import org.mcphoton.world.WorldImpl;
import org.mcphoton.world.Location;
import org.mcphoton.world.World;
import org.mcphoton.world.WorldType;

/**
 * The server configuration, loaded from the file server-config.toml with the Night-Config library.
 *
 * @author TheElectronWill
 */
public final class ServerConfigImpl implements ServerConfiguration {
	private static final transient File FILE = new File(Photon.getMainDirectory(),
														"server-config.toml");

	private volatile String motd = "Default MOTD. Change it in the config.";
	private volatile int maxPlayers = 10;
	private volatile int port = 25565;

	@Conversion(LocationConverter.class)
	private volatile Location spawnLocation;
	private volatile boolean onlineMode = false;

	@Conversion(LoggingLevelConverter.class)
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

	void load(Server theServer) {
		LocationConverter.theServer = theServer;
		if (!FILE.exists()) {
			try {
				Files.copy(ServerConfigImpl.class.getResourceAsStream("/default-config.toml"),
						   FILE.toPath());
			} catch (IOException e) {
				throw new RuntimeException("Unable to copy the default config to " + FILE, e);
			}
		}
		TomlConfig config = new TomlParser().parse(FILE);
		new ObjectConverter().toObject(config, this);
		savedComments = config.getComments();// Remembers the comments
		icon = readIcon();
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
		static Server theServer;// Allows to use the Server instance before it's fully constructed
		// This is needed because the construction of the server needs the config, which needs
		// the worlds, which are in the server instance. The worlds are retrieved before the
		// config is read so this works.

		@Override
		public Location convertToField(String value) {
			// Remove leading ( if any
			if (value.charAt(0) == '(') {
				value = value.substring(1);
			}
			// Remove trailing ) if any
			if (value.charAt(value.length() - 1) == ')') {
				value = value.substring(0, value.length() - 1);
			}
			List<String> parts = StringUtils.split(value, ',');
			double x = Double.parseDouble(parts.get(0).trim());
			double y = Double.parseDouble(parts.get(1).trim());
			double z = Double.parseDouble(parts.get(2).trim());
			String worldName = parts.get(3).trim();
			World world = theServer.getWorld(worldName);
			if (world == null) {
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