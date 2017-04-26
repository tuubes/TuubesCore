package org.mcphoton.impl.server;

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.toml.TomlConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import org.mcphoton.Photon;
import org.mcphoton.server.ServerConfiguration;
import org.mcphoton.utils.Location;

/**
 * The server configuration, loaded from the file server-config.toml with the Night-Config library.
 *
 * @author TheElectronWill
 */
public final class ServerConfigImpl implements ServerConfiguration {
	private static final File configFile = new File(Photon.getMainDirectory(), "server-config.toml");

	private volatile String motd;
	private volatile int maxPlayers, port;
	private volatile Location spawnLocation;
	private volatile boolean onlineMode;
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
		return impl;
	}

	void save() {
		TomlConfig config = new ObjectConverter().toConfig(this, TomlConfig::new);
		config.setComments(savedComments);// Restores the comments
		config.write(configFile);
	}
}
