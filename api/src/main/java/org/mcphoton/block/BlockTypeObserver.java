package org.mcphoton.block;

/**
 * Observes the type changes of a block.
 *
 * @author TheElectronWill
 */
public interface BlockTypeObserver {
	void onTypeChange(BlockType oldType, BlockType newType);
}