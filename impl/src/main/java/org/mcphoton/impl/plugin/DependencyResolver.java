package org.mcphoton.impl.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.mcphoton.plugin.PluginDescription;

/**
 * Solves dependencies between plugins.
 * <h2>How it works</h2>
 * <p>
 * In a loop: we eliminate every plugin that has all its dependencies resolved.
 * </p>
 *
 * @author TheElectronWill
 *
 */
public class DependencyResolver {

	private final Map<String, List<DependencyRequirement>> unresolved = new HashMap<>();
	private final Map<String, String> versions = new HashMap<>();

	public void addAvailable(String name, String version) {
		versions.put(name, version);
	}

	public void addAvailable(List<String> names, List<String> versions) {
		if (names.size() != versions.size()) {
			throw new IllegalArgumentException("The two lists must have the same size.");
		}
		for (Iterator<String> it1 = names.iterator(), it2 = versions.iterator(); it1.hasNext();) {
			String name = it1.next(), version = it2.next();
			this.versions.put(name, version);
		}
	}

	public void addToResolve(PluginDescription description) {
		final String name = description.name(), version = description.version();
		final String[] requiredDependencies = description.requiredDependencies();
		final String[] optionalDependencies = description.optionalDependencies();

		versions.put(name, version);

		if (requiredDependencies.length == 0 && optionalDependencies.length == 0) {
			unresolved.put(name, Collections.emptyList());
			return;
		}

		List<DependencyRequirement> depends = new ArrayList<>();
		unresolved.put(name, depends);

		if (requiredDependencies.length > 0) {
			for (String requiredDependency : requiredDependencies) {
				depends.add(DependencyRequirement.parse(requiredDependency, false));
			}
		}
		if (optionalDependencies.length > 0) {
			for (String optionalDependency : optionalDependencies) {
				depends.add(DependencyRequirement.parse(optionalDependency, true));
			}
		}
	}

	/**
	 * Resolves the dependencies of all plugins that were added by the
	 * {@link #add(org.mcphoton.plugin.Plugin)} method. This method returns a {@link Solution} object, which
	 * contains the best load order and the errors that occured, if any.
	 */
	public Solution resolve() {
		return resolve(new ArrayList<>());
	}

	/**
	 * Resolves the dependencies of all plugins that were added by the
	 * {@link #add(org.mcphoton.plugin.Plugin)} method. This method returns a {@link Solution} object, which
	 * contains the best load order and the errors that occured, if any.
	 */
	public Solution resolve(List<Throwable> errorsList) {
		List<String> resolved = new ArrayList<>();

		int lastSize = -1;
		while (unresolved.size() > 0) {//while there are unresolved dependencies

			Iterator<Entry<String, List<DependencyRequirement>>> unresolvedIterator = unresolved.entrySet().iterator();
			while (unresolvedIterator.hasNext()) {//for each plugin
				Entry<String, List<DependencyRequirement>> entry = unresolvedIterator.next();

				List<DependencyRequirement> depends = entry.getValue();
				String pluginName = entry.getKey();

				if (depends.isEmpty()) {//all dependencies have been solved
					resolved.add(pluginName);//adds it to the resolved list
					unresolvedIterator.remove();//removes it from the unresolved map
				} else {//there are still some dependencies

					Iterator<DependencyRequirement> dependsIterator = depends.iterator();
					while (dependsIterator.hasNext()) {//for each dependency

						DependencyRequirement depend = dependsIterator.next();
						String availableVersion = versions.get(depend.getName());

						if (availableVersion == null) {//dependency not available
							if (depend.isOptional()) {
								dependsIterator.remove();
							}
						} else if (!unresolved.containsKey(depend.getName())) {//this dependency has been resolved and will be loaded... but it's not necessarily the right version!
							if (depend.satisfiesRequirement(availableVersion)) {//it's the right version :)
								dependsIterator.remove();
							}
						}
					}

					if (depends.isEmpty()) {//all dependencies have been solved
						resolved.add(pluginName);//adds it to the resolved list
						unresolvedIterator.remove();//removes it from the unresolved map
					}

				}
			}
			int currentSize = unresolved.size();
			if (currentSize == lastSize) {// aucun changement effectué
				for (Entry<String, List<DependencyRequirement>> entry : unresolved.entrySet()) {// on parcourt tous les plugins
					Exception ex = new UnmetDependenciesException(entry.getKey(), entry.getValue());
					errorsList.add(ex);
				}
				break;
			}
			lastSize = currentSize;

		}
		return new Solution(resolved, errorsList);
	}

	public class Solution {

		public final List<String> resolvedOrder;
		public final List<Throwable> errors;

		public Solution(List<String> loadOrder, List<Throwable> errors) {
			this.resolvedOrder = loadOrder;
			this.errors = errors;
		}

	}

}
