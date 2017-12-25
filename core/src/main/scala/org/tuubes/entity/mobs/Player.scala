package org.tuubes.entity.mobs

import java.util
import java.util.UUID

import com.github.steveice10.mc.protocol.data.game.entity.metadata.{MetadataType, TrackedMetadataValue}
import org.tuubes.entity.MobType
import org.tuubes.item.{Inventory, InventoryHolder, PlayerInventory}
import org.tuubes.user.User

/**
 * @author TheElectronWill
 */
class Player(var name:String, val accountId: UUID)
	extends LivingEntity(PlayerType) with User with InventoryHolder {

	private[this] val inv: PlayerInventory = null
	override def getInventory: Inventory = inv

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

	override def isOnline = true
	override def spawn(): Unit = ???
}
object PlayerType extends MobType("photon.player") {
	override private[tuubes] val id = 0
}