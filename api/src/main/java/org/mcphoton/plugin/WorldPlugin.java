package org.mcphoton.plugin;

import com.electronwill.utils.Constant;
import java.io.File;
import org.mcphoton.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A WorldPlugin is associated with a game world, and isn't aware of what happens in the other
 * worlds.
 *
 * @author TheElectronWill
 */
public abstract class WorldPlugin implements Plugin {
	protected final Logger logger = LoggerFactory.getLogger(getName());
	private final Constant<File> directory = new Constant<>();
	private final Constant<File> configFile = new Constant<>();
	private final Constant<World> world = new Constant<>();
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
		return directory.get();
	}

	@Override
	public final File getConfigFile() {
		return configFile.get();
	}

	@Override
	public final Logger getLogger() {
		return logger;
	}

	/**
	 * Gets the world where this plugin is loaded.
	 */
	public final World getActiveWorld() {
		return world.get();
	}

	public final void init(PluginDescription description, World world) {
		this.description.init(description);
		File directory = new File(world.getDirectory(), getName());
		this.directory.init(directory);
		this.configFile.init(new File(directory, "config.toml"));
	}
}