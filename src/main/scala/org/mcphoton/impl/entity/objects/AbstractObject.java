package org.mcphoton.impl.entity.objects;

import org.mcphoton.impl.entity.AbstractEntity;
import org.mcphoton.world.Location;

/**
 * @author TheElectronWill
 */
public abstract class AbstractObject extends AbstractEntity {
	protected AbstractObject(Location location) {
		super(location);
	}

	@Override
	public abstract AbstractObjectType getType();
}