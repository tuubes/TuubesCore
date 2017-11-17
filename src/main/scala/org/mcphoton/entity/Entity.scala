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
abstract class Entity(val entityType: Type[_]) extends Updatable {
	// ---- Internal entity management ----
	private[this] val atomicId = new AtomicInteger(-1)

	private[mcphoton] def init(id: Int, w: World, eg: ExecutionGroup): Unit = {
		this.synchronized {
			atomicId.set(id)
			_world = w
			oldGroup.set(execGroup)
			execGroup = eg
		}
	}

	private[mcphoton] def entityId: Int = atomicId.get()

	override protected def destroyImpl(): Unit = {
		//TODO world.unregister(this)
		atomicId.set(-1)
	}

	// ---- Public entity state ----
	var velocity: Vec3d = Vec3d.Zero
	@volatile protected[this] var position: Vec3d = Vec3d.Zero
	@volatile protected[this] var _world: World = _

	def location = Location(position, _world)
	def world: World = _world

	protected[this] val dataStorage = new TrackedMetadataStorage({
		val list = new ju.ArrayList[TrackedMetadataValue](dataStorageSizeHint)
		buildDataStorage(list)
		list
	})

	protected def dataStorageSizeHint: Int
	protected def buildDataStorage(values: ju.List[TrackedMetadataValue]): Unit = {}

	/**
	 * Makes the entity appear in its world. This method should register the entity to the world
	 * and send the spawn packets to the near players.
	 */
	def spawn(): Unit
}