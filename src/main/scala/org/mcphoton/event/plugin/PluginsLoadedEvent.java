package org.mcphoton.event.plugin;

import java.util.Collection;
import org.mcphoton.event.Event;
import org.mcphoton.plugin.Plugin;

/**
 * Event fired at startup, after all plugins are loaded.
 *
 * @author TheElectronWill
 *
 */
public class PluginsLoadedEvent implements Event {

	private final Collection<Plugin> loaded;

	public PluginsLoadedEvent(Collection<Plugin> loaded) {
		this.loaded = loaded;
	}

	/**
	 * Gets the plugins that were successfully loaded.
	 */
	public Collection<Plugin> getLoaded() {
		return loaded;
	}

}
