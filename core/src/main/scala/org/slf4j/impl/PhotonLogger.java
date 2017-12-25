package org.slf4j.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import org.tuubes.messaging.Colors;
import org.tuubes.server.LogLevel;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;

/**
 * Logs messages to the console and to the log file.
 * <h2>Log format</h2>
 * <p>
 * <code> HH:mm:ss LEVEL [thread] LoggerName: message</code>
 * </p>
 * <h2>Log files</h2>
 * <p>
 * They are located in the "logs" directory. There's a log file per day. Each log file is named with
 * the day it correspond to. Name format: <code> DD-MM-YYYY.log</code>
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

	private static final boolean USE_COLORS = true;
	private static volatile LogLevel level = LogLevel.INFO;

	public static LogLevel getLevel() {
		return level;
	}

	public static void setLevel(LogLevel level) {
		PhotonLogger.level = level;
	}

	private final String name;

	public PhotonLogger(String name) {
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
	private void formatAndLogArguments(LogLevel level, String msg, Object[] args) {
		if (PhotonLogger.level.compareTo(level) < 0) {
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
	private void formatAndLogArguments(LogLevel level, Colors.V color, String msg, Object[] args) {
		if (!USE_COLORS) {
			formatAndLogArguments(level, msg, args);
			return;
		}
		if (PhotonLogger.level.compareTo(level) < 0) {
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
	private void formatAndLogThrowable(LogLevel level, String msg, Throwable t) {
		if (PhotonLogger.level.compareTo(level) < 0) {
			return;
		}
		LocalDateTime dateTime = LocalDateTime.now();
		String formatted = formatLogLine(level, dateTime, msg);// creates the message's line to be logged
		LoggingService.logThrowable(dateTime, t, formatted);// finally writes
	}

	/**
	 * Formats and logs a message with a Throwable.
	 */
	private void formatAndLogThrowable(LogLevel level, Colors.V color, String msg, Throwable t) {
		if (!USE_COLORS) {
			formatAndLogThrowable(level, msg, t);
			return;
		}
		if (PhotonLogger.level.compareTo(level) < 0) {
			return;
		}
		LocalDateTime dateTime = LocalDateTime.now();
		String formatted = formatLogLine(level, dateTime, msg);// creates the message's line to be logged
		LoggingService.logThrowable(dateTime, t, formatted, color);// finally writes
	}

	/**
	 * Formats a log line.
	 */
	private String formatLogLine(LogLevel level, LocalDateTime dateTime, String msg) {
		StringBuilder sb = new StringBuilder(msg.length() + 20);
		sb.append(TIME_FORMATTER.format(dateTime));
		sb.append(' ').append(level.getAlignedName());
		sb.append(" [").append(Thread.currentThread().getName()).append(']');
		sb.append(' ').append(name);
		sb.append(": ").append(msg);
		return sb.toString();
	}

	@Override
	public boolean isTraceEnabled() {
		return level.compareTo(LogLevel.TRACE) >= 0;
	}

	@Override
	public void trace(String msg) {
		formatAndLogArguments(LogLevel.TRACE, msg, null);
	}

	@Override
	public void trace(String format, Object arg) {
		formatAndLogArguments(LogLevel.TRACE, format, new Object[] {arg});
	}

	@Override
	public void trace(String format, Object arg1, Object arg2) {
		formatAndLogArguments(LogLevel.TRACE, format, new Object[] {arg1, arg2});
	}

	@Override
	public void trace(String format, Object... arguments) {
		formatAndLogArguments(LogLevel.TRACE, format, arguments);
	}

	@Override
	public void trace(String msg, Throwable t) {
		formatAndLogThrowable(LogLevel.TRACE, msg, t);
	}

	@Override
	public boolean isDebugEnabled() {
		return level.compareTo(LogLevel.DEBUG) >= 0;
	}

	@Override
	public void debug(String msg) {
		formatAndLogArguments(LogLevel.DEBUG, msg, null);
	}

	@Override
	public void debug(String format, Object arg) {
		formatAndLogArguments(LogLevel.DEBUG, format, new Object[] {arg});
	}

	@Override
	public void debug(String format, Object arg1, Object arg2) {
		formatAndLogArguments(LogLevel.DEBUG, format, new Object[] {arg1, arg2});
	}

	@Override
	public void debug(String format, Object... arguments) {
		formatAndLogArguments(LogLevel.DEBUG, format, arguments);
	}

	@Override
	public void debug(String msg, Throwable t) {
		formatAndLogThrowable(LogLevel.DEBUG, msg, t);
	}

	@Override
	public boolean isInfoEnabled() {
		return level.compareTo(LogLevel.INFO) >= 0;
	}

	@Override
	public void info(String msg) {
		formatAndLogArguments(LogLevel.INFO, msg, null);
	}

	@Override
	public void info(String format, Object arg) {
		formatAndLogArguments(LogLevel.INFO, format, new Object[] {arg});
	}

	@Override
	public void info(String format, Object arg1, Object arg2) {
		formatAndLogArguments(LogLevel.INFO, format, new Object[] {arg1, arg2});
	}

	@Override
	public void info(String format, Object... arguments) {
		formatAndLogArguments(LogLevel.INFO, format, arguments);
	}

	@Override
	public void info(String msg, Throwable t) {
		formatAndLogThrowable(LogLevel.INFO, msg, t);
	}

	@Override
	public boolean isWarnEnabled() {
		return level.compareTo(LogLevel.WARN) >= 0;
	}

	@Override
	public void warn(String msg) {
		formatAndLogArguments(LogLevel.WARN, Colors.Gold(), msg, null);
	}

	@Override
	public void warn(String format, Object arg) {
		formatAndLogArguments(LogLevel.WARN, Colors.Gold(), format, new Object[] {arg});
	}

	@Override
	public void warn(String format, Object arg1, Object arg2) {
		formatAndLogArguments(LogLevel.WARN, Colors.Gold(), format, new Object[] {arg1, arg2});
	}

	@Override
	public void warn(String format, Object... arguments) {
		formatAndLogArguments(LogLevel.WARN, Colors.Gold(), format, arguments);
	}

	@Override
	public void warn(String msg, Throwable t) {
		formatAndLogThrowable(LogLevel.WARN, Colors.Gold(), msg, t);
	}

	@Override
	public boolean isErrorEnabled() {
		return level.compareTo(LogLevel.ERROR) >= 0;
	}

	@Override
	public void error(String msg) {
		formatAndLogArguments(LogLevel.ERROR, Colors.Red(), msg, null);
	}

	@Override
	public void error(String format, Object arg) {
		formatAndLogArguments(LogLevel.ERROR, Colors.Red(), format, new Object[] {arg});
	}

	@Override
	public void error(String format, Object arg1, Object arg2) {
		formatAndLogArguments(LogLevel.ERROR, Colors.Red(), format, new Object[] {arg1, arg2});
	}

	@Override
	public void error(String format, Object... arguments) {
		formatAndLogArguments(LogLevel.ERROR, Colors.Red(), format, arguments);
	}

	@Override
	public void error(String msg, Throwable t) {
		formatAndLogThrowable(LogLevel.ERROR, Colors.Red(), msg, t);
	}
}