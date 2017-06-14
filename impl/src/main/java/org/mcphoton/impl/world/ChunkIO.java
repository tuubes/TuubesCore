package org.mcphoton.impl.world;

import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.io.buffer.ByteBufferNetInput;
import com.github.steveice10.packetlib.io.stream.StreamNetOutput;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.mcphoton.world.World;

/**
 * Reads and writes chunks (columns) asynchronously. There is one instance of ChunkIO per world.
 *
 * @author TheElectronWill
 */
public final class ChunkIO {
	private final Path chunksDirectory;

	public ChunkIO(World world) {
		this(new File(world.getDirectory(), "chunks"));
	}

	public ChunkIO(File chunksDirectory) {
		this.chunksDirectory = chunksDirectory.toPath();
		if (!chunksDirectory.isDirectory()) {
			chunksDirectory.mkdir();
		}
	}

	/**
	 * Gets the Path to the file containing the data of a particular chunk.
	 *
	 * @param x the chunk X coordinate
	 * @param z the chunk Z coordinate
	 * @return the Path of the chunk's file
	 */
	private Path getChunkPath(int x, int z) {
		return chunksDirectory.resolve(x + "_" + z + ".chunk");
	}

	/**
	 * Checks if a chunk is present on the disk.
	 *
	 * @param x the chunk X coordinate
	 * @param z the chunk Z coordinate
	 * @return {@code true} iff it is on the disk
	 */
	boolean isChunkOnDisk(int x, int z) {
		return Files.exists(getChunkPath(x, z));
	}

	/**
	 * Deletes the data of a chunk.
	 *
	 * @param x the chunk X coordinate
	 * @param z the chunk Z coordinate
	 * @throws IOException if an I/O error occurs.
	 */
	void deleteChunk(int x, int z) throws IOException {
		Files.deleteIfExists(getChunkPath(x, z));
	}

	/**
	 * Writes a chunk, asynchronously. This method returns (almost) immediately and executes the IO
	 * operations in the background. The completionHandler is notified when the operations are
	 * completed.
	 *
	 * @param chunk             the chunk to write
	 * @param attachment        an object that will be given to the completionHandler
	 * @param completionHandler handles the completion or failure of the operations
	 * @param <A>               the attachment's type
	 * @throws IOException if an I/O error occurs in the foreground (for instance if the chunk
	 *                     file cannot be opened). If an error occurs in background, the
	 *                     method completionHandler.failed is called.
	 */
	<A> void writeChunk(ChunkColumnImpl chunk, A attachment,
						CompletionHandler<ChunkColumnImpl, A> completionHandler)
			throws IOException {
		Path chunkPath = getChunkPath(chunk.getX(), chunk.getZ());
		try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(chunkPath,
																			StandardOpenOption.WRITE)) {
			// Writes the data to an extensible Stream
			ServerChunkDataPacket chunkPacket = new ServerChunkDataPacket(chunk.getLibColumn());
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream(8192);
			NetOutput netOutput = new StreamNetOutput(byteStream);
			chunkPacket.write(netOutput);
			// Copies the data to a ByteBuffer
			ByteBuffer buffer = ByteBuffer.wrap(
					byteStream.toByteArray());//TODO avoid copying: use array directly
			// Creates a custom handler to hide IO details from the caller
			CompletionHandler<Integer, A> lowLevelhandler = new CompletionHandler<Integer, A>() {
				@Override
				public void completed(Integer result, A attachment) {
					completionHandler.completed(chunk, attachment);
				}

				@Override
				public void failed(Throwable exc, A attachment) {
					completionHandler.failed(exc, attachment);
				}
			};
			// Asynchronously writes the ByteBuffer to the channel
			channel.write(buffer, 0, attachment, lowLevelhandler);
		}
	}

	/**
	 * Reads a chunk, asynchronously. This method returns (almost) immediately and executes the IO
	 * operations in the background. The completionHandler is notified when the operations are
	 * completed.
	 *
	 * @param x                 the X chunk coordinate
	 * @param z                 the Z chunk coordinate
	 * @param attachment        an object that will be given to the completionHandler
	 * @param completionHandler handles the completion or failure of the operations
	 * @param <A>               the attachment's type
	 * @throws IOException if an I/O error occurs in the foreground (for instance if the chunk
	 *                     file cannot be opened). If an error occurs in background, the
	 *                     method completionHandler.failed is called.
	 */
	<A> void readChunk(int x, int z, A attachment,
					   CompletionHandler<ChunkColumnImpl, A> completionHandler) throws IOException {
		Path chunkPath = getChunkPath(x, z);
		if (!Files.exists(chunkPath)) {
			completionHandler.failed(new NoSuchFileException("The chunk file doesn't exist."),
									 attachment);
		}
		try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(chunkPath,
																			StandardOpenOption.READ)) {
			// Allocates a ByteBuffer of the size of the chunk file
			int fileSize = (int)channel.size();
			ByteBuffer buffer = ByteBuffer.allocate(fileSize);
			// Creates a custom handler to hide IO details from the caller and to parse the chunk
			CompletionHandler<Integer, A> lowLevelHandler = new CompletionHandler<Integer, A>() {
				@Override
				public void completed(Integer result, A attachment) {
					NetInput netInput = new ByteBufferNetInput(buffer);
					ServerChunkDataPacket packet = new ServerChunkDataPacket();
					try {
						packet.read(netInput);
					} catch (IOException e) {
						completionHandler.failed(e, attachment);
					}
					ChunkColumnImpl chunk = new ChunkColumnImpl(packet.getColumn());
					completionHandler.completed(chunk, attachment);
				}

				@Override
				public void failed(Throwable exc, A attachment) {
					completionHandler.failed(exc, attachment);
				}
			};
			// Asynchronously reads the file into the ByteBuffer and parses it with the handler
			channel.read(buffer, 0, attachment, lowLevelHandler);
		}
	}
}