package org.mcphoton.block;

import java.util.Optional;

/**
 * The captured state of a block.
 *
 * @author TheElectronWill
 */
public class BlockState {
	private final BlockType type;
	private final BlockEntity entity;

	public BlockState(BlockType type) {
		this(type, null);
	}

	public BlockState(BlockType type, BlockEntity entity) {
		this.type = type;
		this.entity = entity;
	}

	/**
	 * @return the block's type
	 */
	public BlockType getType() {
		return type;
	}

	/**
	 * @return the block's "entity", if present, or Optional.empty() if not
	 */
	public Optional<BlockEntity> getEntity() {
		return Optional.ofNullable(entity);
	}
}