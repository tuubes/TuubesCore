package org.mcphoton.impl.world;

import com.github.steveice10.mc.protocol.data.game.chunk.Column;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.mcphoton.Photon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A memory-sensitive cache that uses {@link SoftReference} to stores the chunks of one world.
 *
 * @author TheElectronWill
 */
public final class ChunkCache {
	private static final Logger log = LoggerFactory.getLogger(ChunkCache.class);
	private final WorldImpl world;

	public ChunkCache(WorldImpl world) {
		this.world = world;
	}

	private final ConcurrentMap<ChunkCoordinates, CacheValue> chunksMap = new ConcurrentHashMap<>(512);
	private final ReferenceQueue<ChunkColumnImpl> collectedChunks = new ReferenceQueue<>();

	private static final class CacheValue extends SoftReference<ChunkColumnImpl> {
		private final ChunkCoordinates key;// Used to remove the value from the map when collected
		private final Column libColumn;// Used to save the chunk's data when collected

		CacheValue(ChunkCoordinates key, ChunkColumnImpl value,
				   ReferenceQueue<? super ChunkColumnImpl> queue) {
			super(value, queue);
			this.key = key;
			this.libColumn = value.getLibColumn();
		}
	}

	private void cleanCollectedChunks() {
		for (CacheValue value; (value = (CacheValue)collectedChunks.poll()) != null; ) {
			/*
			If the chunk has been modified, put it back into the map, so that, if it's
			requested while the ChunkIO is saving it, we can get it immediately without waiting
			for the IO to terminates. That way we avoid some problems. In particular, a
			situation where ChunkCache.get() reads some outdated data before the chunk is saved
			cannot occur.

			If the chunk isn't needed anymore then it will be collected again, and since it
			won't have been modified it won't be written to the disk. Eventually it will be
			discarded by the GC.
			*/
			if (value.libColumn.hasChanged()) {
				ChunkColumnImpl chunkColumn = new ChunkColumnImpl(value.libColumn);
				ChunkCoordinates coords = new ChunkCoordinates(chunkColumn.getX(),
															   chunkColumn.getZ());
				CacheValue newValue = new CacheValue(coords, chunkColumn, collectedChunks);
				chunksMap.put(value.key, newValue);
				CompletionHandler<ChunkColumnImpl, Object> completionHandler = new CompletionHandler<ChunkColumnImpl, Object>() {
					@Override
					public void completed(ChunkColumnImpl result, Object attachment) {
						log.debug("Chunk saved: world {}, x={}, z={}", world, result.getX(),
								  result.getZ());
						//Let the reference go if the chunk isn't used
					}

					@Override
					public void failed(Throwable exc, Object attachment) {
						log.error("Failed to save chunk in world {}, x={}, z={}", world.getName(),
								  coords.x, coords.z, exc);
					}
				};
				world.chunkIO.writeChunk(chunkColumn, null, completionHandler);
			}
		}
	}

	/**
	 * Returns a chunk column from the cache.
	 *
	 * @param x the chunk X coordinate
	 * @param z the chunk Z coordinate
	 * @return the chunk with the given coordinates, or {@code null} if it's not in the cache
	 */
	public ChunkColumnImpl getCached(int x, int z) {
		log.debug("In world {}: ChunkCache.getCached(x={}, z={})", world.getName(), x, z);
		cleanCollectedChunks();// Processes the collected references
		ChunkCoordinates coords = new ChunkCoordinates(x, z);
		CacheValue ref = chunksMap.get(coords);
		return (ref == null) ? null : ref.get();
	}

	/**
	 * Asynchronously gets a chunk column. The completionHandler is called when the chunk is
	 * available or when there is an error.
	 * <p>
	 * If the chunk exists in the cache, the completionHandler is called immediately with
	 * that chunk. If the chunk doesn't exist in the cache, then it is either read from the
	 * disk or generated with the world's ChunkGenerator, and the completionHandler is
	 * notified later, <b>from another thread</b>.
	 *
	 * @param x                 the chunk X coordinate
	 * @param z                 the chunk Z coordinate
	 * @param attachment        an object to give to the completionHandler
	 * @param completionHandler the handler that will be notified of the success or failure of
	 *                          the operation
	 */
	public <A> void getAsync(int x, int z, A attachment,
							 CompletionHandler<ChunkColumnImpl, A> completionHandler) {
		log.debug("In world {}: ChunkCache.getAsync(x={}, z={}, attachment={})", world.getName(), x,
				  z, attachment);
		cleanCollectedChunks();// Processes the collected references
		ChunkCoordinates coords = new ChunkCoordinates(x, z);
		CacheValue ref = chunksMap.get(coords);
		ChunkColumnImpl chunk;
		if (ref != null && (chunk = ref.get()) != null) {// Chunk in cache
			completionHandler.completed(chunk, attachment);
		} else {// Chunk not in cache
			ChunkIO chunkIO = world.chunkIO;
			if (chunkIO.isChunkOnDisk(x, z)) {// Chunk on disk
				chunkIO.readChunk(x, z, attachment, completionHandler);//async read
			} else {// Chunk needs to be generated
				Runnable task = () -> {
					ChunkColumnImpl generatedChunk = (ChunkColumnImpl)world.chunkGenerator.generate(
							x, z);
					chunksMap.put(coords, new CacheValue(coords, generatedChunk, collectedChunks));
					completionHandler.completed(generatedChunk, attachment);
				};
				Photon.getExecutorService().execute(task);//async generation
			}
		}
	}
}