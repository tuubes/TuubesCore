package org.mcphoton.entity.mobs;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.MetadataType;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.TrackedMetadataValue;
import java.util.List;
import org.mcphoton.world.Location;

/**
 * @author TheElectronWill
 */
public abstract class LivingMob extends AbstractMob {
	protected LivingMob(Location location) {
		super(location);
	}

	@Override
	protected List<TrackedMetadataValue> initializeDataStorage() {
		List<TrackedMetadataValue> values = super.initializeDataStorage();
		values.add(new TrackedMetadataValue(MetadataType.BYTE, 0));//hands status
		values.add(new TrackedMetadataValue(MetadataType.FLOAT, 1.0));// HP
		values.add(new TrackedMetadataValue(MetadataType.VARINT, 0));//potion effect color
		values.add(new TrackedMetadataValue(MetadataType.BOOLEAN, false));//is potion effect ambient
		values.add(new TrackedMetadataValue(MetadataType.VARINT, 0));//number of stuck arrows
		return values;
	}
}
