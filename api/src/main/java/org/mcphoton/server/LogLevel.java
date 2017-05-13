package org.mcphoton.server;

/**
 * Log level enum.
 *
 * @author TheElectronWill
 */
public enum LogLevel {
	ERROR("ERROR"), WARN("WARN "), INFO("INFO "), DEBUG("DEBUG"), TRACE("TRACE");

	private final String alignedName;

	LogLevel(String alignedName) {
		this.alignedName = alignedName;
	}

	public String getAlignedName() {
		return alignedName;
	}
}