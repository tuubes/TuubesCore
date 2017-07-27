package org.mcphoton.impl.plugin;

import com.electronwill.utils.SimpleBag;
import java.util.Collection;
import java.util.Collections;
import org.mcphoton.plugin.Plugin;
import org.mcphoton.plugin.PluginDescription;
import org.mcphoton.world.World;

/**
 *
 * @author TheElectronWill
 */
public class PluginInfos {

	public final Class<? extends Plugin> clazz;
	public final PluginClassLoader classLoader;
	public final PluginDescription description;
	public Collection<World> worlds;

	public PluginInfos(Class<? extends Plugin> clazz, PluginClassLoader classLoader, PluginDescription description) {
		this.clazz = clazz;
		this.classLoader = classLoader;
		this.description = description;
	}

	public Collection<World> getWorlds() {
		if (worlds == null) {
			worlds = Collections.synchronizedCollection(new SimpleBag<>()); //synchronized because any thread could use it
		}
		return worlds;
	}

	public void setWorlds(Collection<World> worlds) {
		Collection<World> copy = new SimpleBag<>(worlds.size(), 1);
		copy.addAll(worlds); //copy the data so that each plugin gets its own list of world.
		this.worlds = Collections.synchronizedCollection(copy); //synchronized because any thread could use it
	}

}
