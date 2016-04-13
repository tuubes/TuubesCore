package org.mcphoton.impl.plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.mcphoton.plugin.Plugin;

/**
 * Solves dependencies between plugins.
 * <h2>How it works</h2>
 * <p>
 * In a loop: we eliminate every plugin that only has met (resolved) dependencies.
 * </p>
 *
 * @author TheElectronWill
 *
 */
public class DependancyResolver {

	private final Map<String, List<DependencyRequirement>> unresolved = new HashMap<>();
	private final Map<String, String> versions = new HashMap<>();

	public void add(Plugin plugin) {
		versions.put(plugin.getName(), plugin.getVersion());

		if (plugin.getRequiredDependencies() == null && plugin.getOptionalDependencies() == null) {
			unresolved.put(plugin.getName(), Collections.emptyList());
			return;
		}

		List<DependencyRequirement> depends = new ArrayList<>();
		unresolved.put(plugin.getName(), depends);

		if (plugin.getRequiredDependencies() != null) {
			for (String requiredDependency : plugin.getRequiredDependencies()) {
				depends.add(DependencyRequirement.parse(requiredDependency, false));
			}
		}
		if (plugin.getOptionalDependencies() != null) {
			for (String optionalDependency : plugin.getOptionalDependencies()) {
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
		List<String> resolved = new ArrayList<>();
		List<Exception> errors = new ArrayList<>();

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
					errors.add(ex);
				}
				break;
			}
			lastSize = currentSize;

		}
		return new Solution(resolved, errors);
	}

	public class Solution {

		public final List<String> resolvedOrder;
		public final List<Exception> errors;

		public Solution(List<String> loadOrder, List<Exception> errors) {
			this.resolvedOrder = loadOrder;
			this.errors = errors;
		}

	}

}
