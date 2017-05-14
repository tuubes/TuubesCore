package org.mcphoton.impl.block;

import org.mcphoton.Photon;
import org.mcphoton.block.BlockType;
import org.mcphoton.impl.AbstractType;

/**
 * @author TheElectronWill
 */
public abstract class AbstractBlockType extends AbstractType implements BlockType {
	private final int id;

	protected AbstractBlockType(String uniqueName) {
		super(uniqueName);
		this.id = Photon.getGameRegistry().registerBlock(this);
	}

	public final int getId() {
		return id;
	}

	@Override
	public boolean isVariant(BlockType other) {
		if(other instanceof AbstractBlockType) {
			return ((AbstractBlockType)other).id >> 4 == id >> 4;
		}
		return false;
	}
}