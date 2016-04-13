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

	public static final int LEVEL_ERROR = 0, LEVEL_WARN = 1, LEVEL_INFO = 2, LEVEL_DEBUG = 3, LEVEL_TRACE = 4;
	private static final String[] LEVEL_STRINGS = {"ERROR", " WARN", " INFO", "DEBUG", " TRACE"};// aligned names
	private static final boolean USE_COLORS = !System.getProperty("os.name").toLowerCase().contains("windows") && System.console() != null;

	private volatile int level;
	private final String name;

	public PhotonLogger(String name) {
		this(LEVEL_INFO, name);
	}

	public PhotonLogger(int level, String name) {
		this.level = level;
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
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
	private void formatAndLogArguments(int level, String msg, Object[] args) {
		if (this.level < level) {
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
	private void formatAndLogArguments(int level, Color color, String msg, Object[] args) {
		if (!USE_COLORS) {
			formatAndLogArguments(level, msg, args);
			return;
		}
		if (this.level < level) {
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
	private void formatAndLogThrowable(int level, String msg, Throwable t) {
		if (this.level < level) {
			return;
		}
		LocalDateTime dateTime = LocalDateTime.now();
		String formatted = formatLogLine(level, dateTime, msg);// creates the message's line to be logged
		LoggingService.logThrowable(dateTime, t, formatted);// finally writes
	}

	/**
	 * Formats and logs a message with a Throwable.
	 */
	private void formatAndLogThrowable(int level, Color color, String msg, Throwable t) {
		if (!USE_COLORS) {
			formatAndLogThrowable(level, msg, t);
			return;
		}
		if (this.level < level) {
			return;
		}
		LocalDateTime dateTime = LocalDateTime.now();
		String formatted = formatLogLine(level, dateTime, msg);// creates the message's line to be logged
		LoggingService.logThrowable(dateTime, t, formatted, color);// finally writes
	}

	/**
	 * Formats a log line.
	 */
	private String formatLogLine(int level, LocalDateTime dateTime, String msg) {
		StringBuilder sb = new StringBuilder(msg.length() + 20);
		sb.append(TIME_FORMATTER.format(dateTime));
		sb.append(' ').append(LEVEL_STRINGS[level]);
		sb.append(" [").append(Thread.currentThread().getName()).append(']');
		sb.append(' ').append(name);
		sb.append(": ").append(msg);
		return sb.toString();
	}

	@Override
	public boolean isTraceEnabled() {
		return level >= LEVEL_TRACE;
	}

	@Override
	public void trace(String msg) {
		formatAndLogArguments(LEVEL_TRACE, msg, null);
	}

	@Override
	public void trace(String format, Object arg) {
		formatAndLogArguments(LEVEL_TRACE, format, new Object[] {arg});
	}

	@Override
	public void trace(String format, Object arg1, Object arg2) {
		formatAndLogArguments(LEVEL_TRACE, format, new Object[] {arg1, arg2});
	}

	@Override
	public void trace(String format, Object... arguments) {
		formatAndLogArguments(LEVEL_TRACE, format, arguments);
	}

	@Override
	public void trace(String msg, Throwable t) {
		formatAndLogThrowable(LEVEL_TRACE, msg, t);
	}

	@Override
	public boolean isDebugEnabled() {
		return level >= LEVEL_DEBUG;
	}

	@Override
	public void debug(String msg) {
		formatAndLogArguments(LEVEL_DEBUG, msg, null);
	}

	@Override
	public void debug(String format, Object arg) {
		formatAndLogArguments(LEVEL_DEBUG, format, new Object[] {arg});
	}

	@Override
	public void debug(String format, Object arg1, Object arg2) {
		formatAndLogArguments(LEVEL_DEBUG, format, new Object[] {arg1, arg2});
	}

	@Override
	public void debug(String format, Object... arguments) {
		formatAndLogArguments(LEVEL_DEBUG, format, arguments);
	}

	@Override
	public void debug(String msg, Throwable t) {
		formatAndLogThrowable(LEVEL_DEBUG, msg, t);
	}

	@Override
	public boolean isInfoEnabled() {
		return level >= LEVEL_INFO;
	}

	@Override
	public void info(String msg) {
		formatAndLogArguments(LEVEL_INFO, msg, null);
	}

	@Override
	public void info(String format, Object arg) {
		formatAndLogArguments(LEVEL_INFO, format, new Object[] {arg});
	}

	@Override
	public void info(String format, Object arg1, Object arg2) {
		formatAndLogArguments(LEVEL_INFO, format, new Object[] {arg1, arg2});
	}

	@Override
	public void info(String format, Object... arguments) {
		formatAndLogArguments(LEVEL_INFO, format, arguments);
	}

	@Override
	public void info(String msg, Throwable t) {
		formatAndLogThrowable(LEVEL_INFO, msg, t);
	}

	@Override
	public boolean isWarnEnabled() {
		return level >= LEVEL_WARN;
	}

	@Override
	public void warn(String msg) {
		formatAndLogArguments(LEVEL_WARN, Color.GOLD, msg, null);
	}

	@Override
	public void warn(String format, Object arg) {
		formatAndLogArguments(LEVEL_WARN, Color.GOLD, format, new Object[] {arg});
	}

	@Override
	public void warn(String format, Object arg1, Object arg2) {
		formatAndLogArguments(LEVEL_WARN, Color.GOLD, format, new Object[] {arg1, arg2});
	}

	@Override
	public void warn(String format, Object... arguments) {
		formatAndLogArguments(LEVEL_WARN, Color.GOLD, format, arguments);
	}

	@Override
	public void warn(String msg, Throwable t) {
		formatAndLogThrowable(LEVEL_WARN, Color.GOLD, msg, t);
	}

	@Override
	public boolean isErrorEnabled() {
		return level >= LEVEL_ERROR;
	}

	@Override
	public void error(String msg) {
		formatAndLogArguments(LEVEL_ERROR, Color.RED, msg, null);
	}

	@Override
	public void error(String format, Object arg) {
		formatAndLogArguments(LEVEL_ERROR, Color.RED, format, new Object[] {arg});
	}

	@Override
	public void error(String format, Object arg1, Object arg2) {
		formatAndLogArguments(LEVEL_ERROR, Color.RED, format, new Object[] {arg1, arg2});
	}

	@Override
	public void error(String format, Object... arguments) {
		formatAndLogArguments(LEVEL_ERROR, Color.RED, format, arguments);
	}

	@Override
	public void error(String msg, Throwable t) {
		formatAndLogThrowable(LEVEL_ERROR, Color.RED, msg, t);
	}

}
