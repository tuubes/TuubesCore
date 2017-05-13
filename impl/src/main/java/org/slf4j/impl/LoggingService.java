package org.slf4j.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;
import org.mcphoton.Photon;
import org.mcphoton.messaging.Color;

/**
 * Writes the log messages to the console and to the disk, and manages log file rotation.
 *
 * @author TheElectronWill
 */
public final class LoggingService {

	private static final DateTimeFormatter FILE_NAME_FORMATTER = new DateTimeFormatterBuilder()
			.appendValue(DAY_OF_MONTH, 2)
			.appendLiteral('-')
			.appendValue(MONTH_OF_YEAR, 2)
			.appendLiteral('-')
			.appendValue(YEAR, 4)
			.appendLiteral(".log")
			.toFormatter();

	private static final String ANSI_RESET = "\u001B[0m";// ansi code for resetting the color of the console
	private static final File LOGS_DIR = new File(Photon.getMainDirectory(), "logs");

	static {
		if (!LOGS_DIR.isDirectory()) {
			LOGS_DIR.mkdir();
		}
	}

	private static final PrintStream consoleOut = System.out;
	private static PrintWriter logWriter;
	private static File logFile;
	private static int lastDay = -1;

	public static synchronized void logLine(LocalDateTime dateTime, String line) {
		rotateIfNeeded(dateTime);
		consoleOut.println(line);
		logWriter.println(line);
	}

	public static synchronized void logLine(LocalDateTime dateTime, String line, Color color) {
		rotateIfNeeded(dateTime);
		consoleOut.print(color.ansi);
		consoleOut.print(line);
		consoleOut.println(ANSI_RESET);
		logWriter.println(line);
	}

	public static synchronized void logThrowable(LocalDateTime dateTime, Throwable t, String message) {
		rotateIfNeeded(dateTime);

		consoleOut.println(message);
		t.printStackTrace(consoleOut);

		logWriter.println(message);
		t.printStackTrace(logWriter);
	}

	public static synchronized void logThrowable(LocalDateTime dateTime, Throwable t, String message, Color color) {
		rotateIfNeeded(dateTime);

		consoleOut.print(color.ansi);
		consoleOut.println(message);
		t.printStackTrace(consoleOut);
		consoleOut.print(ANSI_RESET);

		logWriter.println(message);
		t.printStackTrace(logWriter);
	}

	public static void close() {
		consoleOut.close();
		if (logWriter != null) {
			logWriter.close();
		}
	}

	private static void rotateIfNeeded(LocalDateTime dateTime) {
		int currentDay = dateTime.getDayOfMonth();
		if (dateTime.getDayOfMonth() != lastDay) {
			lastDay = currentDay;
			try {
				String newFileName = FILE_NAME_FORMATTER.format(dateTime);
				logFile = new File(LOGS_DIR, newFileName);
				logWriter = new PrintWriter(new FileWriter(logFile, true));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private LoggingService() {
	}

}
