package org.mcphoton.block;

import java.util.Optional;

/**
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

	public BlockType getType() {
		return type;
	}

	public Optional<BlockEntity> getEntity() {
		return Optional.ofNullable(entity);
	}
}