package org.mcphoton.entity

import java.util.concurrent.atomic.AtomicInteger
import java.{util => ju}

import com.electronwill.utils.Vec3d
import com.github.steveice10.mc.protocol.data.game.entity.metadata.{TrackedMetadataStorage, TrackedMetadataValue}
import org.mcphoton.Type
import org.mcphoton.runtime.{ExecutionGroup, Updatable}
import org.mcphoton.world.{Location, World}

/**
 * @author TheElectronWill
 */
abstract class Entity(val `type`: Type[_]) extends Updatable {
	private[this] val atomicId = new AtomicInteger(-1)
	private[mcphoton] def setId(id: Int): Unit = atomicId.set(id)
	def getId: Int = atomicId.get()

	override protected def destroyImpl(): Unit = {
		//TODO world.unregister(this)
		atomicId.set(-1)
	}

	@volatile protected[this] var position: Vec3d = Vec3d.Zero
	@volatile protected[this] var velocity: Vec3d = Vec3d.Zero
	@volatile protected[this] var world: World = _
	@volatile protected[this] var executionGroup: ExecutionGroup = _

	def getLocation: Location = Location(position, world)
	def getVelocity: Vec3d = velocity
	def setVelocity(vel: Vec3d): Unit = velocity = vel
	def getWorld: World = world
	def getExecutionGroup: ExecutionGroup = executionGroup

	protected[this] val dataStorage = new TrackedMetadataStorage({
		val list = new ju.ArrayList[TrackedMetadataValue](dataStorageSizeHint)
		buildDataStorage(list)
		list
	})

	protected def dataStorageSizeHint: Int
	protected def buildDataStorage(values: ju.List[TrackedMetadataValue]): Unit

	/**
	 * Makes the entity appear in its world. This method should register the entity to the world
	 * and send the spawn packets to the near players.
	 */
	def spawn(): Unit
}