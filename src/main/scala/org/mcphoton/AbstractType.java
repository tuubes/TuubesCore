package org.mcphoton;

import org.mcphoton.utils.Type;

/**
 * @author TheElectronWill
 */
public abstract class AbstractType implements Type {
	protected final String uniqueName;

	protected AbstractType(String uniqueName) {
		this.uniqueName = uniqueName;
	}

	@Override
	public final String getUniqueName() {
		return uniqueName;
	}
}