/*
 * Copyright (c) 2016 MCPhoton <http://mcphoton.org> and contributors.
 *
 * This file is part of the Photon API <https://github.com/mcphoton/Photon-API>.
 *
 * The Photon API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Photon API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
