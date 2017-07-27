package org.mcphoton.impl.item;

import java.util.OptionalInt;
import org.mcphoton.Photon;
import org.mcphoton.impl.AbstractType;
import org.mcphoton.impl.GameRegistry;
import org.mcphoton.item.ItemType;

/**
 * @author TheElectronWill
 */
public abstract class AbstractItemType extends AbstractType implements ItemType {
	private final int id;
	private final OptionalInt damageData;

	protected AbstractItemType(String uniqueName) {
		super(uniqueName);
		GameRegistry.ItemTypeInfos infos = Photon.getGameRegistry().registerItem(this);
		id = infos.id;
		damageData = infos.damageData;
	}

	public final int getId() {
		return id;
	}

	public OptionalInt getDamageData() {
		return damageData;
	}
}