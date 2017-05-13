package org.mcphoton.plugin;

import com.electronwill.utils.Constant;
import java.io.File;
import java.util.Collection;
import org.mcphoton.Photon;
import org.mcphoton.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A GlobalPlugin is loaded for the entire server (unlike a WorldPlugin).
 *
 * @author TheElectronWill
 */
public abstract class GlobalPlugin implements Plugin {
	protected final Logger logger = LoggerFactory.getLogger(getName());
	private final File directory = new File(Photon.getPluginsDirectory(), getName());
	private final File configFile = new File(directory, "config.toml");
	private final Constant<Collection<World>> worlds = new Constant<>();
	private final Constant<PluginDescription> description = new Constant<>();

	@Override
	public String getName() {
		return description.get().name();
	}

	@Override
	public String getVersion() {
		return description.get().version();
	}

	@Override
	public String getAuthor() {
		return description.get().author();
	}

	@Override
	public String[] getOptionalDependencies() {
		return description.get().optionalDependencies();
	}

	@Override
	public String[] getRequiredDependencies() {
		return description.get().requiredDependencies();
	}

	@Override
	public final File getDirectory() {
		return directory;
	}

	@Override
	public final File getConfigFile() {
		return configFile;
	}

	@Override
	public final Logger getLogger() {
		return logger;
	}

	/**
	 * Gets the worlds where this plugin is loaded.
	 */
	public final Collection<World> getActiveWorlds() {
		return worlds.get();
	}

	public final void init(PluginDescription description, Collection<World> worlds) {
		this.description.init(description);
		this.worlds.init(worlds);
	}
}