package org.mcphoton.entity

import com.electronwill.utils.Rotation2
import com.github.steveice10.mc.protocol.data.game.entity.metadata.{MetadataType, TrackedMetadataValue}
import org.mcphoton.Type
import java.{util => ju}

/**
 * @author TheElectronWill
 */
abstract class BasicEntity(t: Type[_]) extends Entity(t) {
	protected[this] val bodyRotation: Rotation2 = Rotation2.Zero

	override protected def buildDataStorage(values: ju.List[TrackedMetadataValue]): Unit = {
		values.add(new TrackedMetadataValue(MetadataType.BYTE, 0))//various states
		values.add(new TrackedMetadataValue(MetadataType.VARINT, 300))//air level
		values.add(new TrackedMetadataValue(MetadataType.STRING, ""))//custom name
		values.add(new TrackedMetadataValue(MetadataType.BOOLEAN, false))//custom name visible
		values.add(new TrackedMetadataValue(MetadataType.BOOLEAN, false))//silent
		values.add(new TrackedMetadataValue(MetadataType.BOOLEAN, false))//no gravity
	}
}