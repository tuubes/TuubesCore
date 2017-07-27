package org.mcphoton.block;

import java.util.Optional;
import java.util.function.Consumer;
import org.mcphoton.utils.Location;

/**
 * A reference to a block.
 *
 * @author TheElectronWill
 */
public interface BlockRef {
	/**
	 * @return the block's location
	 */
	Location getLocation();

	/**
	 * Returns the block's type. If the only way to get the type is to read or create a Chunk,
	 * then it is done, and the getType() methods blocks until the block's type is known.
	 *
	 * @return the block's type
	 */
	BlockType getType();

	/**
	 * Returns the block's type, if it is immediately available. If the block's chunk isn't
	 * loaded, returns an empty Optional.
	 *
	 * @return the block's type, or {@code Optional.empty()} if the block is unavailable
	 */
	Optional<BlockType> getTypeOptional();

	/**
	 * Calls the given callback with the block's type, as soon as it is available.
	 *
	 * @param callback the function to call when the block's type is obtained
	 * @return {@code true} if the callback will be called later, {@code false}
	 * if it has been called immediately
	 */
	boolean getType(Consumer<BlockType> callback);

	/**
	 * Sets the block' type.
	 *
	 * @param type the type to set
	 */
	void setType(BlockType type);

	/**
	 * @return the block's entity
	 */
	Optional<BlockEntity> getEntity();

	void addTypeObserver(BlockTypeObserver observer);

	void removeTypeObserver(BlockTypeObserver observer);
}