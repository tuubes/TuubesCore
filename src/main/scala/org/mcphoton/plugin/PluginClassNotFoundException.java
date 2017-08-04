package org.mcphoton.plugin;

import java.io.File;

/**
 *
 * @author TheElectronWill
 */
public class PluginClassNotFoundException extends Exception {

	public PluginClassNotFoundException(String message) {
		super(message);
	}

	public PluginClassNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public PluginClassNotFoundException(File pluginFile) {
		super("No suitable plugin class found in " + pluginFile);
	}

}
