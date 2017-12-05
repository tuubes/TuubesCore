package org.mcphoton.runtime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Provides asynchronous IO facilities.
 *
 * @author TheElectronWill
 */
public final class IOSystem {
	private IOSystem() {}

	static final ExecutorService executor = Executors.newCachedThreadPool(
			new CountingThreadFactory("IOThread_"));

	/**
	 * Executes an IOTask in the background. The submitted task is executed at some time in the
	 * future, without blocking the caller of this method.
	 * <p>
	 * <b>Important note: the IOSystem is made for IO operations, not for CPU-intensive
	 * calculations. Please submit the CPU-intensive tasks to the TaskSystem instead of the
	 * IOSystem.</b>
	 *
	 * @param task         the task to execute
	 * @param errorHandler the handler to call if an error occurs
	 */
	public static void execute(IOTask task, Consumer<IOException> errorHandler) {
		executor.execute(() -> {
			try {
				task.apply();
			} catch (IOException e) {
				errorHandler.accept(e);
			}
		});
	}

	/**
	 * Asynchronously opens and uses a FileChannel.
	 * <p>
	 * The FileChannel is automatically closed at the end of the task.
	 *
	 * @param file         the file to open
	 * @param task         the task to execute on the file
	 * @param errorHandler the handler to call if an error occurs
	 * @param openOptions  the open options
	 */
	public static void useFile(File file, IOConsumer<FileChannel> task,
							   Consumer<IOException> errorHandler, OpenOption... openOptions) {
		executor.execute(() -> {
			try (FileChannel channel = FileChannel.open(file.toPath(), openOptions)) {
				task.apply(channel);
			} catch (IOException e) {
				errorHandler.accept(e);
			}
		});
	}

	/**
	 * Asynchronously opens a File, creates an InputStream and uses it.
	 * <p>
	 * The InputStream is automatically closed at the end of the task.
	 *
	 * @param file         the file to open
	 * @param task         the task to execute on the InputStream
	 * @param errorHandler the handler to call if an error occurs
	 * @param openOptions  the open options
	 */
	public static void useFileInput(File file, IOConsumer<InputStream> task,
									Consumer<IOException> errorHandler, OpenOption... openOptions) {
		if (openOptions == null || openOptions.length == 0) {
			openOptions = new OpenOption[]{StandardOpenOption.READ};
		}
		final OpenOption[] finalOptions = openOptions;
		executor.execute(() -> {
			try (InputStream stream = Files.newInputStream(file.toPath(), finalOptions)) {
				task.apply(stream);
			} catch (IOException e) {
				errorHandler.accept(e);
			}
		});
	}

	/**
	 * Asynchronously opens a File, creates an InputStream and uses it.
	 * <p>
	 * The OutputStream is automatically closed at the end of the task.
	 *
	 * @param file         the file to open
	 * @param task         the task to execute on the file
	 * @param errorHandler the handler to call if an error occurs
	 * @param openOptions  the open options
	 */
	public static void useFileOutput(File file, IOConsumer<OutputStream> task,
									 Consumer<IOException> errorHandler,
									 OpenOption... openOptions) {
		if (openOptions == null || openOptions.length == 0) {
			openOptions = new OpenOption[]{StandardOpenOption.WRITE};
		}
		final OpenOption[] finalOptions = openOptions;
		executor.execute(() -> {
			try (OutputStream stream = Files.newOutputStream(file.toPath(), finalOptions)) {
				task.apply(stream);
			} catch (IOException e) {
				errorHandler.accept(e);
			}
		});
	}

	/**
	 * Asynchronously opens and use a connection to an url.
	 * <p>
	 * If the url denotes an web (http) resource, the connection is automatically closed at
	 * the end of the task. Otherwise you have to close the streams that you create.
	 *
	 * @param url          the url to connect to
	 * @param task         the task to execute on the url
	 * @param errorHandler the handler to call if an error occurs
	 */
	public static void useUrl(String url, IOConsumer<URLConnection> task,
							  Consumer<IOException> errorHandler) {
		executor.execute(() -> {
			URLConnection connection = null;
			try {
				connection = new URL(url).openConnection();
				task.apply(connection);
			} catch (IOException e) {
				errorHandler.accept(e);
			} finally {
				// Disconnects if the connection is HTTP
				if (connection instanceof HttpURLConnection) {
					((HttpURLConnection)connection).disconnect();
				}
			}
		});
	}

	@FunctionalInterface
	public interface IOConsumer<T> {
		void apply(T t) throws IOException;
	}

	@FunctionalInterface
	public interface IOTask {
		void apply() throws IOException;
	}
}