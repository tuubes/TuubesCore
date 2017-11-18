package org.mcphoton.entity.mobs

import java.util

import com.github.steveice10.mc.protocol.data.game.entity.metadata.{MetadataType, TrackedMetadataValue}
import org.mcphoton.entity.MobType

/**
 * @author TheElectronWill
 */
abstract class LivingEntity(t: MobType) extends MobEntity(t) {
	override protected def buildDataStorage(values: util.List[TrackedMetadataValue]): Unit = {
		super.buildDataStorage(values)
		values.add(new TrackedMetadataValue(MetadataType.BYTE, 0))//hand state
		values.add(new TrackedMetadataValue(MetadataType.FLOAT, 1f))// HP
		values.add(new TrackedMetadataValue(MetadataType.VARINT, 0))// potion effect color
		values.add(new TrackedMetadataValue(MetadataType.BOOLEAN, false))// is potion effect ambiant
		values.add(new TrackedMetadataValue(MetadataType.VARINT, 0))// number of stuck arrows
	}
	override protected def dataStorageSizeHint = super.dataStorageSizeHint + 5
}