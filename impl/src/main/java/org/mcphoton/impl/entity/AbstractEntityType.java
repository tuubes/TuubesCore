package org.mcphoton.impl.entity;

import org.mcphoton.Photon;
import org.mcphoton.entity.EntityType;
import org.mcphoton.impl.AbstractType;

/**
 * @author TheElectronWill
 */
public abstract class AbstractEntityType extends AbstractType implements EntityType {
	private final int id;

	protected AbstractEntityType(String uniqueName) {
		super(uniqueName);
		this.id = Photon.getGameRegistry().registerEntity(this);
	}

	public final int getId() {
		return id;
	}
}