package org.mcphoton.plugin;

/**
 * @author TheElectronWill
 */
public enum PluginState {
	/**
	 * The plugin has been loaded from disk but isn't enabled yet.
	 */
	LOADED,

	/**
	 * The plugin is enabled.
	 */
	ENABLED,

	/**
	 * The plugin is completely disabled. It cannot be enabled again.
	 */
	DISABLED
}