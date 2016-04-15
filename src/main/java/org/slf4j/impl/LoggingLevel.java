package org.slf4j.impl;

/**
 *
 * @author TheElectronWill
 */
public enum LoggingLevel {

	ERROR(0), WARN(1), INFO(2), DEBUG(3), TRACE(4);

	private final int id;

	private LoggingLevel(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

}
