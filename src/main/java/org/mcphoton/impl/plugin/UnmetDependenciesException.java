package org.mcphoton.impl.plugin;

import java.util.List;

/**
 *
 * @author TheElectronWill
 */
public class UnmetDependenciesException extends Exception {

	private final String pluginName;
	private final List<DependencyRequirement> dependencies;

	public UnmetDependenciesException(String pluginName, List<DependencyRequirement> dependencies) {
		this.pluginName = pluginName;
		this.dependencies = dependencies;
	}

	@Override
	public String getMessage() {
		return "The plugin " + pluginName + " has unmet dependencies: " + dependencies;
	}

	public String getPluginName() {
		return pluginName;
	}

	public List<DependencyRequirement> getDependencies() {
		return dependencies;
	}

}
