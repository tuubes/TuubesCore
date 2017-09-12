package org.mcphoton.world

import com.electronwill.utils.StringUtils._
import com.electronwill.utils.{Vec3d, Vec3i}

/**
 * An immutable location in a world.
 *
 * @author TheElectronWill
 */
abstract class Location(val world: World) {
	def x: Double

	def y: Double

	def z: Double

	def blockX: Int

	def blockY: Int

	def blockZ: Int

	def toVec3d: Vec3d

	def toVec3i: Vec3i
}

/**
 * A location based on a Vec3d
 */
private final class Location3d(val coords: Vec3d, override val world: World) extends Location(world) {
	override def x: Double = coords.x

	override def y: Double = coords.y

	override def z: Double = coords.z

	override def blockX: Int = x.toInt

	override def blockY: Int = y.toInt

	override def blockZ: Int = z.toInt

	override def toVec3d: Vec3d = coords

	override def toVec3i: Vec3i = coords

	override def toString: String = s"$x,$y,$z,${world.getName}"
}

/**
 * A location based on a Vec3i
 */
private final class Location3i(val coords: Vec3i, override val world: World) extends Location(world) {
	override def x: Double = coords.x

	override def y: Double = coords.y

	override def z: Double = coords.z

	override def blockX: Int = coords.x

	override def blockY: Int = coords.y

	override def blockZ: Int = coords.z

	override def toVec3d: Vec3d = coords

	override def toVec3i: Vec3i = coords

	override def toString: String = s"$blockX,$blockY,$blockZ,${world.getName}"
}

object Location {
	/* Creates a new Location from a Vec3d and a World */
	def apply(coords: Vec3d, world: World): Location = {
		new Location3d(coords, world)
	}

	/* Creates a new Location from a Vec3i and a World */
	def apply(coords: Vec3i, world: World): Location = {
		new Location3i(coords, world)
	}

	/* Creates a new Location from 3 double coordinates and a World */
	def apply(x: Double, y: Double, z: Double, world: World): Location = {
		new Location3d(new Vec3d(x, y, z), world)
	}

	/* Creates a new Location from 3 integer coordinates and a World */
	def apply(x: Int, y: Int, z: Int, world: World): Location = {
		new Location3i(new Vec3i(x, y, z), world)
	}

	/* Converts a Location to a Vec3d */
	implicit def toVec3d(location: Location): Vec3d = location.toVec3d

	/* Converts a Location to a Vec3d */
	implicit def toVec3i(location: Location): Vec3i = location.toVec3i

	def fromString(s: String): Location = {
		val splitted = split(s, ',')
		val s0 = splitted.get(0)
		val s1 = splitted.get(1)
		val s2 = splitted.get(2)
		val s3 = splitted.get(3)
		val sCoords = s0 + s1 + s2
		if (contains(sCoords, '.', 'e', 'E')) {
			new Location3d(Vec3d.fromString(sCoords), WorldManager.get(s3).get)
		} else {
			new Location3i(Vec3i.fromString(sCoords), WorldManager.get(s3).get)
		}
	}
}