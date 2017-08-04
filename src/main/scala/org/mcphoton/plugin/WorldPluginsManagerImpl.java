package org.mcphoton.plugin;

import com.electronwill.utils.SimpleBag;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mcphoton.plugin.DependencyResolver.Solution;
import static org.mcphoton.plugin.GlobalPluginsManagerImpl.GLOBAL_CLASS_SHARER;

import org.mcphoton.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of WorldPluginsManager.
 *
 * @author TheElectronWill
 */
public final class WorldPluginsManagerImpl implements WorldPluginsManager {

	private static final Logger log = LoggerFactory.getLogger(WorldPluginsManagerImpl.class);
	private final Map<String, Plugin> plugins = new HashMap<>();
	private final World world;

	public WorldPluginsManagerImpl(World world) {
		this.world = world;
	}

	@Override
	public Plugin getPlugin(String name) {
		return plugins.get(name);
	}

	@Override
	public boolean isPluginLoaded(String name) {
		return plugins.containsKey(name);
	}

	@Override
	public Plugin loadPlugin(File file) throws Exception {
		final PluginClassLoader classLoader = new PluginClassLoader(file.toURI().toURL(), GLOBAL_CLASS_SHARER);
		final Class<? extends Plugin> clazz = PluginClassFinder.findPluginClass(file, classLoader);
		if (clazz == null) {
			throw new PluginClassNotFoundException(file);
		}
		final PluginDescription description = clazz.getAnnotation(PluginDescription.class);
		final Plugin instance = clazz.newInstance();

		if (GlobalPlugin.class.isAssignableFrom(clazz)) {
			if (description == null) {
				throw new MissingPluginDescriptionException(clazz);
			}
			Collection<World> worlds = Collections.synchronizedCollection(new SimpleBag<>(world));
			((GlobalPlugin) instance).init(description, worlds);
		} else if (WorldPlugin.class.isAssignableFrom(clazz)) {
			if (description == null) {
				throw new MissingPluginDescriptionException(clazz);
			}
			((WorldPlugin) instance).init(description, world);
		}

		instance.onLoad();
		classLoader.increaseUseCount();
		plugins.put(instance.getName(), instance);
		return instance;
	}

	@Override
	public List<Plugin> loadPlugins(File[] files) {
		final Map<String, PluginInfos> infosMap = new HashMap<>();
		final List<Plugin> loadedPlugins = new ArrayList<>(files.length);
		final List<Throwable> errors = new ArrayList<>();

		//1: Gather informations about the plugins: class + description.
		log.debug("Gathering informations about the plugins...");
		for (File file : files) {
			try {
				PluginClassLoader classLoader = new PluginClassLoader(file.toURI().toURL(), GLOBAL_CLASS_SHARER);
				Class<? extends Plugin> clazz = PluginClassFinder.findPluginClass(file, classLoader);
				if (clazz == null) {
					throw new PluginClassNotFoundException(file);
				}
				/*
				 * Here we DO need a PluginDescription for every plugin because to resolve the dependencies we
				 * need to have some informations about the plugin before creating its instance.
				 */
				PluginDescription description = clazz.getAnnotation(PluginDescription.class);
				if (description == null) {
					throw new MissingPluginDescriptionException(clazz);
				}
				PluginInfos infos = new PluginInfos(clazz, classLoader, description);
				infosMap.put(description.name(), infos);
				log.trace("Valid plugin found: {} -> infos: {}.", file, infos);
			} catch (Exception | NoClassDefFoundError error) {
				errors.add(error);
			}
		}

		//2: Resolve the dependencies.
		log.debug("Resolving plugins' dependencies...");
		DependencyResolver resolver = new DependencyResolver();
		for (PluginInfos infos : infosMap.values()) {
			resolver.addToResolve(infos.description);
		}
		Solution solution = resolver.resolve(errors);
		log.debug("Solution: {}", solution.resolvedOrder);

		//3: Print informations.
		log.info("{} out of {} server plugins will be loaded.", solution.resolvedOrder.size(), files.length);
		for (Throwable ex : solution.errors) {
			log.error(ex.toString());
		}

		//4: Load the plugins.
		log.debug("Loading the plugins...");
		for (String plugin : solution.resolvedOrder) {
			try {
				PluginInfos infos = infosMap.get(plugin);
				Plugin instance = infos.clazz.newInstance();

				if (GlobalPlugin.class.isAssignableFrom(infos.clazz)) {
					Collection<World> worlds = Collections.synchronizedCollection(new SimpleBag<>(world));
					((GlobalPlugin) instance).init(infos.description, worlds);
				} else if (WorldPlugin.class.isAssignableFrom(infos.clazz)) {
					((WorldPlugin) instance).init(infos.description, world);
				}

				instance.onLoad();
				infos.classLoader.increaseUseCount();
				plugins.put(instance.getName(), instance);
				loadedPlugins.add(instance);
			} catch (Exception ex) {
				log.error("Unable to load the plugin {} in world {}.", plugin, world.getName());
			}
		}
		return loadedPlugins;
	}

	@Override
	public void registerPlugin(Plugin plugin) {
		plugins.put(plugin.getName(), plugin);
	}

	@Override
	public void unloadAllPlugins() {
		for (Plugin plugin : plugins.values()) {
			try {
				unloadPlugin(plugin);
			} catch (Exception ex) {
				log.error("Unable to unload the plugin {}", plugin, ex);
			}
		}
	}

	@Override
	public void unloadPlugin(Plugin plugin) throws Exception {
		plugin.onUnload();
		SharedClassLoader classLoader = (SharedClassLoader) plugin.getClass().getClassLoader();
		classLoader.decreaseUseCount();
		GLOBAL_CLASS_SHARER.removeUselessClassLoader(classLoader);
		plugins.remove(plugin.getName(), plugin);
	}

	@Override
	public void unloadPlugin(String name) throws Exception {
		unloadPlugin(plugins.get(name));
	}

	@Override
	public void unregisterPlugin(Plugin plugin) {
		plugins.remove(plugin.getName(), plugin);
	}

}
