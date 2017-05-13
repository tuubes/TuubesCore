package org.mcphoton.impl.server;

/**
 * Thrown when the server fails to start.
 *
 * @author TheElectronWill
 */
public class StartupFailedException extends RuntimeException {
	public StartupFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public StartupFailedException(Throwable cause) {
		super("Cannot start the server because of an unrecoverable error", cause);
	}
}
