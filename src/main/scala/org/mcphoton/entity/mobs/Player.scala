package org.mcphoton.entity.mobs

import java.util
import java.util.UUID

import com.github.steveice10.mc.protocol.data.game.entity.metadata.{MetadataType, TrackedMetadataValue}
import org.mcphoton.entity.MobType
import org.mcphoton.user.User

/**
 * @author TheElectronWill
 */
	override protected def dataStorageSizeHint: Int = ???
class Player(var name:String, val accountId: UUID) extends LivingEntity(PlayerType) with User {
	override def update(dt: Double): Unit = ???
	override def sendUpdates(): Unit = ???

	override protected def buildDataStorage(values: util.List[TrackedMetadataValue]): Unit = {
		super.buildDataStorage(values)
		values.add(new TrackedMetadataValue(MetadataType.FLOAT, 0f))// additional HP
		values.add(new TrackedMetadataValue(MetadataType.VARINT, 0))// score
		values.add(new TrackedMetadataValue(MetadataType.BYTE, 0))// skin parameters
		values.add(new TrackedMetadataValue(MetadataType.BYTE, 1))// main hand (1 right, 0 left)
		// TODO empty nbt tag for these two values:
		values.add(new TrackedMetadataValue(MetadataType.NBT_TAG, null))// left shoulder parrot
		values.add(new TrackedMetadataValue(MetadataType.NBT_TAG, null))// right shoulder parrot
	}
	override protected def dataStorageSizeHint: Int = super.dataStorageSizeHint + 6

}
object PlayerType extends MobType("photon.player") {
	override private[mcphoton] val id = 0
}