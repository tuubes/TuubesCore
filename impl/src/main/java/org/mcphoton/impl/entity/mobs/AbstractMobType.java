package org.mcphoton.impl.entity.mobs;

import org.mcphoton.Photon;
import org.mcphoton.entity.EntityType;
import org.mcphoton.impl.AbstractType;

/**
 * @author TheElectronWill
 */
public abstract class AbstractMobType extends AbstractType implements EntityType {
	private final int id;

	protected AbstractMobType(String uniqueName) {
		super(uniqueName);
		this.id = Photon.getGameRegistry().registerMob(this);
	}

	public final int getId() {
		return id;
	}
}