package org.mcphoton.entity.objects;

import org.mcphoton.entity.AbstractEntity;
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