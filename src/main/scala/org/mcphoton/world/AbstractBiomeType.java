package org.mcphoton.world;

import java.util.Locale;
import org.mcphoton.Photon;
import org.mcphoton.AbstractType;

/**
 * @author TheElectronWill
 */
public abstract class AbstractBiomeType extends AbstractType implements BiomeType {
	private final int id;
	protected AbstractBiomeType(String uniqueName) {
		super(uniqueName);
		id = Photon.getGameRegistry().registerBiome(this);
	}

	@Override
	public String getLocalizedName(Locale locale) {
		return null;
	}

	public final int getId() {
		return id;
	}
}