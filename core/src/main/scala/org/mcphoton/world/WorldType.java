package org.mcphoton.world;

/**
 * Defines a type of world.
 *
 * @author TheElectronWill
 */
public enum WorldType {
	OVERWORLD(0), NETHER(-1), END(1);

	public final int id;

	WorldType(int id) {
		this.id = id;
	}
}