/*
 * Copyright (c) 2016 MCPhoton <http://mcphoton.org> and contributors.
 *
 * This file is part of the Photon Server Implementation <https://github.com/mcphoton/Photon-Server>.
 *
 * The Photon Server Implementation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Photon Server Implementation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.slf4j.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import org.mcphoton.messaging.Color;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

/**
 * Logs messages to the console and to the log file.
 * <h2>Log format</h2>
 * <p>
 * <code> HH:mm:ss LEVEL [thread] LoggerName: message</code>
 * </p>
 * <h2>Log files</h2>
 * <p>
 * They are located in the "logs" directory. There's a log file per day. Each log file is named with the day
 * it correspond to. Name format: <code> DD-MM-YYYY.log</code>
 * </p>
 *
 * @author TheElectronWill
 *
 */
public final class PhotonLogger extends MarkerIgnoringBase {

	private static final DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder()
			.appendValue(HOUR_OF_DAY, 2)
			.appendLiteral(':')
			.appendValue(MINUTE_OF_HOUR, 2)
			.appendLiteral(':')
			.appendValue(SECOND_OF_MINUTE, 2)
			.toFormatter();

	private static final boolean USE_COLORS = !System.getProperty("os.name").toLowerCase().contains("windows") && System.console() != null;
	private static volatile LoggingLevel level;

	public static LoggingLevel getLevel() {
		return level;
	}

	public static void setLevel(LoggingLevel level) {
		PhotonLogger.level = level;
	}

	private final String name;

	public PhotonLogger(String name) {
		this(LoggingLevel.INFO, name);
	}

	public PhotonLogger(LoggingLevel level, String name) {
		PhotonLogger.level = level;
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * Formats and logs a message with arguments.
	 *
	 * @param args the arguments, may be null
	 */
	private void formatAndLogArguments(LoggingLevel level, String msg, Object[] args) {
		if (PhotonLogger.level.id < level.id) {
			return;
		}
		LocalDateTime dateTime = LocalDateTime.now();
		FormattingTuple ft = MessageFormatter.arrayFormat(msg, args);// formats arguments
		String formatted = formatLogLine(level, dateTime, ft.getMessage());// creates the message's line to be logged
		Throwable t = ft.getThrowable();
		if (t == null) {
			LoggingService.logLine(dateTime, formatted);
		} else {
			LoggingService.logThrowable(dateTime, t, formatted);
		}
	}

	/**
	 * Formats and logs a message with arguments.
	 *
	 * @param args the arguments, may be null
	 */
	private void formatAndLogArguments(LoggingLevel level, Color color, String msg, Object[] args) {
		if (!USE_COLORS) {
			formatAndLogArguments(level, msg, args);
			return;
		}
		if (PhotonLogger.level.id < level.id) {
			return;
		}
		LocalDateTime dateTime = LocalDateTime.now();
		FormattingTuple ft = MessageFormatter.arrayFormat(msg, args);// formats arguments
		String formatted = formatLogLine(level, dateTime, ft.getMessage());// creates the message's line to be logged
		Throwable t = ft.getThrowable();
		if (t == null) {
			LoggingService.logLine(dateTime, formatted, color);
		} else {
			LoggingService.logThrowable(dateTime, t, formatted, color);
		}
	}

	/**
	 * Formats and logs a message with a Throwable.
	 */
	private void formatAndLogThrowable(LoggingLevel level, String msg, Throwable t) {
		if (PhotonLogger.level.id < level.id) {
			return;
		}
		LocalDateTime dateTime = LocalDateTime.now();
		String formatted = formatLogLine(level, dateTime, msg);// creates the message's line to be logged
		LoggingService.logThrowable(dateTime, t, formatted);// finally writes
	}

	/**
	 * Formats and logs a message with a Throwable.
	 */
	private void formatAndLogThrowable(LoggingLevel level, Color color, String msg, Throwable t) {
		if (!USE_COLORS) {
			formatAndLogThrowable(level, msg, t);
			return;
		}
		if (PhotonLogger.level.id < level.id) {
			return;
		}
		LocalDateTime dateTime = LocalDateTime.now();
		String formatted = formatLogLine(level, dateTime, msg);// creates the message's line to be logged
		LoggingService.logThrowable(dateTime, t, formatted, color);// finally writes
	}

	/**
	 * Formats a log line.
	 */
	private String formatLogLine(LoggingLevel level, LocalDateTime dateTime, String msg) {
		StringBuilder sb = new StringBuilder(msg.length() + 20);
		sb.append(TIME_FORMATTER.format(dateTime));
		sb.append(' ').append(level.displayName);
		sb.append(" [").append(Thread.currentThread().getName()).append(']');
		sb.append(' ').append(name);
		sb.append(": ").append(msg);
		return sb.toString();
	}

	@Override
	public boolean isTraceEnabled() {
		return level.id >= LoggingLevel.TRACE.id;
	}

	@Override
	public void trace(String msg) {
		formatAndLogArguments(LoggingLevel.TRACE, msg, null);
	}

	@Override
	public void trace(String format, Object arg) {
		formatAndLogArguments(LoggingLevel.TRACE, format, new Object[] {arg});
	}

	@Override
	public void trace(String format, Object arg1, Object arg2) {
		formatAndLogArguments(LoggingLevel.TRACE, format, new Object[] {arg1, arg2});
	}

	@Override
	public void trace(String format, Object... arguments) {
		formatAndLogArguments(LoggingLevel.TRACE, format, arguments);
	}

	@Override
	public void trace(String msg, Throwable t) {
		formatAndLogThrowable(LoggingLevel.TRACE, msg, t);
	}

	@Override
	public boolean isDebugEnabled() {
		return level.id >= LoggingLevel.DEBUG.id;
	}

	@Override
	public void debug(String msg) {
		formatAndLogArguments(LoggingLevel.DEBUG, msg, null);
	}

	@Override
	public void debug(String format, Object arg) {
		formatAndLogArguments(LoggingLevel.DEBUG, format, new Object[] {arg});
	}

	@Override
	public void debug(String format, Object arg1, Object arg2) {
		formatAndLogArguments(LoggingLevel.DEBUG, format, new Object[] {arg1, arg2});
	}

	@Override
	public void debug(String format, Object... arguments) {
		formatAndLogArguments(LoggingLevel.DEBUG, format, arguments);
	}

	@Override
	public void debug(String msg, Throwable t) {
		formatAndLogThrowable(LoggingLevel.DEBUG, msg, t);
	}

	@Override
	public boolean isInfoEnabled() {
		return level.id >= LoggingLevel.INFO.id;
	}

	@Override
	public void info(String msg) {
		formatAndLogArguments(LoggingLevel.INFO, msg, null);
	}

	@Override
	public void info(String format, Object arg) {
		formatAndLogArguments(LoggingLevel.INFO, format, new Object[] {arg});
	}

	@Override
	public void info(String format, Object arg1, Object arg2) {
		formatAndLogArguments(LoggingLevel.INFO, format, new Object[] {arg1, arg2});
	}

	@Override
	public void info(String format, Object... arguments) {
		formatAndLogArguments(LoggingLevel.INFO, format, arguments);
	}

	@Override
	public void info(String msg, Throwable t) {
		formatAndLogThrowable(LoggingLevel.INFO, msg, t);
	}

	@Override
	public boolean isWarnEnabled() {
		return level.id >= LoggingLevel.WARN.id;
	}

	@Override
	public void warn(String msg) {
		formatAndLogArguments(LoggingLevel.WARN, Color.GOLD, msg, null);
	}

	@Override
	public void warn(String format, Object arg) {
		formatAndLogArguments(LoggingLevel.WARN, Color.GOLD, format, new Object[] {arg});
	}

	@Override
	public void warn(String format, Object arg1, Object arg2) {
		formatAndLogArguments(LoggingLevel.WARN, Color.GOLD, format, new Object[] {arg1, arg2});
	}

	@Override
	public void warn(String format, Object... arguments) {
		formatAndLogArguments(LoggingLevel.WARN, Color.GOLD, format, arguments);
	}

	@Override
	public void warn(String msg, Throwable t) {
		formatAndLogThrowable(LoggingLevel.WARN, Color.GOLD, msg, t);
	}

	@Override
	public boolean isErrorEnabled() {
		return level.id >= LoggingLevel.ERROR.id;
	}

	@Override
	public void error(String msg) {
		formatAndLogArguments(LoggingLevel.ERROR, Color.RED, msg, null);
	}

	@Override
	public void error(String format, Object arg) {
		formatAndLogArguments(LoggingLevel.ERROR, Color.RED, format, new Object[] {arg});
	}

	@Override
	public void error(String format, Object arg1, Object arg2) {
		formatAndLogArguments(LoggingLevel.ERROR, Color.RED, format, new Object[] {arg1, arg2});
	}

	@Override
	public void error(String format, Object... arguments) {
		formatAndLogArguments(LoggingLevel.ERROR, Color.RED, format, arguments);
	}

	@Override
	public void error(String msg, Throwable t) {
		formatAndLogThrowable(LoggingLevel.ERROR, Color.RED, msg, t);
	}

}
