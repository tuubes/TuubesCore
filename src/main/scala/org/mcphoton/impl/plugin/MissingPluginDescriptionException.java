package org.mcphoton.impl.plugin;

/**
 *
 * @author TheElectronWill
 */
public class MissingPluginDescriptionException extends Exception {

	public MissingPluginDescriptionException(String message) {
		super(message);
	}

	public MissingPluginDescriptionException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingPluginDescriptionException(Class pluginClass) {
		super("Missing annotation @PluginDescription for class " + pluginClass);
	}

}
