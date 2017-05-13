package org.mcphoton.impl.plugin;

import java.util.List;

/**
 *
 * @author TheElectronWill
 */
public class UnmetDependenciesException extends Exception {

	private final List<DependencyRequirement> dependencies;
	private final String pluginName;

	public UnmetDependenciesException(String pluginName, List<DependencyRequirement> dependencies) {
		this.pluginName = pluginName;
		this.dependencies = dependencies;
	}

	public List<DependencyRequirement> getDependencies() {
		return dependencies;
	}

	@Override
	public String getMessage() {
		return "The plugin " + pluginName + " has unmet dependencies: " + dependencies;
	}

	public String getPluginName() {
		return pluginName;
	}

}
