package org.mcphoton.impl.entity.objects;

import org.mcphoton.Photon;
import org.mcphoton.entity.EntityType;
import org.mcphoton.impl.AbstractType;

/**
 * Basic type for "object" entities.
 *
 * @author TheElectronWill
 */
public abstract class AbstractObjectType extends AbstractType implements EntityType {
	private final int id;

	protected AbstractObjectType(String uniqueName) {
		super(uniqueName);
		this.id = Photon.getGameRegistry().registerObject(this);
	}

	public final int getId() {
		return id;
	}
}