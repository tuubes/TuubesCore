package org.mcphoton.world;

import com.electronwill.utils.Bag;
import com.electronwill.utils.SimpleBag;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.nio.channels.CompletionHandler;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.mcphoton.block.BlockEntity;
import org.mcphoton.block.BlockRef;
import org.mcphoton.block.BlockType;
import org.mcphoton.block.BlockTypeObserver;
import org.mcphoton.world.Location;

/**
 * A BlockRef that
 *
 * @author TheElectronWill
 */
final class BlockRefImpl implements BlockRef {
	//TODO cache the BlockRef so that World.getBlockRef(x,y,z) returns the available BlockRef if any

	// Unmodifiable informations
	private final WorldImpl world;
	private final int x, y, z;

	// Changing informations
	private volatile Reference<ChunkColumnImpl> chunkReference;
	private volatile BlockType type, typeToSet;
	private volatile AtomicBoolean asyncGetStarted;
	private final Bag<BlockTypeObserver> observers = new SimpleBag<>();

	BlockRefImpl(WorldImpl world, int x, int y, int z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.chunkReference = new WeakReference<>(null);
		world.getChunkCache()
			 .getAsync(x / 16, z / 16, null, new CompletionHandler<ChunkColumnImpl, Object>() {
				 @Override
				 public void completed(ChunkColumnImpl result, Object attachment) {
					 chunkReference = new SoftReference<>(result);
				 }

				 @Override
				 public void failed(Throwable exc, Object attachment) {
					 //What to do?
				 }
			 });
	}

	@Override
	public Location getLocation() {
		return new Location(x, y, z, world);
	}

	@Override
	public BlockType getType() {
		if (type == null) {
			ChunkColumnImpl chunkColumn = world.getChunkCache().getSync(x / 16, z / 16);
			chunkReference = new WeakReference<>(chunkColumn);
			type = chunkColumn.getBlockType(x % 16, y, z % 16);
		}
		return type;
	}

	@Override
	public Optional<BlockType> getTypeOptional() {
		return Optional.of(type);
	}

	@Override
	public boolean getType(Consumer<BlockType> callback) {
		if (type == null) {
			CompletionHandler<ChunkColumnImpl, Object> completionHandler = new CompletionHandler<ChunkColumnImpl, Object>() {
				@Override
				public void completed(ChunkColumnImpl result, Object attachment) {
					chunkReference = new WeakReference<>(result);
					type = result.getBlockType(x % 16, y, z % 16);
					callback.accept(type);
				}

				@Override
				public void failed(Throwable exc, Object attachment) {
					//:'(
				}
			};
			world.getChunkCache().getAsync(x / 16, z / 16, null, completionHandler);
			return true;
		} else {
			callback.accept(type);
			return false;
		}
	}

	@Override
	public void setType(BlockType type) {
		cachedType = type;
		ChunkColumnImpl chunkColumn = chunkReference.get();
		if (chunkColumn == null) {//The chunk is gone => set the block type asynchronously
			// Checks if an async get has not been started. The check is done with compareAndSet
			// and is therefore thread-safe: when multiple threads are at this point, only one
			// can get canStartAsyncGet = true, the others will get false.
			boolean canStartAsyncGet = asyncGetStarted.compareAndSet(false, true);
			if (canStartAsyncGet) {
				CompletionHandler<ChunkColumnImpl, Object> completionHandler = new CompletionHandler<ChunkColumnImpl, Object>() {
					@Override
					public void completed(ChunkColumnImpl result, Object attachment) {
						chunkReference = new SoftReference<>(result);
						result.setBlockType(x % 16, y, z % 16, typeToSet);
						// Sets the block type to the last value of cachedType, that is, to the
						// last value that has been given to the setType method.
					}

					@Override
					public void failed(Throwable exc, Object attachment) {
						//What to do?
					}
				};
				world.getChunkCache().getAsync(x / 16, z / 16, null, completionHandler);
			}
		} else {
			chunkColumn.setBlockType(x % 16, y, z % 16, type);
		}
	}

	@Override
	public Optional<BlockEntity> getEntity() {
		return Optional.empty();//TODO BlockEntities aren't implemented yet
	}

	@Override
	public void addTypeObserver(BlockTypeObserver observer) {

	}

	@Override
	public void removeTypeObserver(BlockTypeObserver observer) {

	}
}