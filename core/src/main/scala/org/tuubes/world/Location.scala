package org.tuubes.world

import com.electronwill.utils.StringUtils._
import com.electronwill.utils.{Vec3d, Vec3i}
import org.tuubes.server.PhotonServer

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

	override def toString: String = s"$x,$y,$z,${world.name}"
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

	override def toString: String = s"$blockX,$blockY,$blockZ,${world.name}"
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
		val fpClues = Array('.', 'e', 'E')
		val world = PhotonServer.world(s3).get
		if (contains(s0, fpClues) || contains(s1, fpClues) || contains(s2, fpClues)) {
			new Location3d(new Vec3d(s0.toDouble, s1.toDouble, s2.toDouble), world)
		} else {
			new Location3i(new Vec3i(s0.toInt, s1.toInt, s2.toInt), world)
		}
	}
}