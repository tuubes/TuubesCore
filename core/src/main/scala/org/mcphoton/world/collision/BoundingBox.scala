package org.mcphoton.world.collision

import com.electronwill.utils.Vec3d
import org.mcphoton.block.BlockFace

/**
 * @author TheElectronWill
 */
final class BoundingBox(val lower: Vec3d, val upper: Vec3d) {
	private def this(cx: Double, cy: Double, cz: Double, dx: Double, dy: Double, dz: Double) = {
		this(new Vec3d(cx - dx, cy - dy, cz - dz), new Vec3d(cx + dx, cy + dy, cz + dz))
	}
	def this(center: Vec3d, width: Double, height: Double, depth: Double) = {
		this(center.x, center.y, center.z, width / 2.0, height / 2.0, depth / 2.0)
	}

	def inflate(x: Double, y: Double, z: Double): BoundingBox = {
		inflate(new Vec3d(x, y, z))
	}

	def inflate(iv: Vec3d): BoundingBox = {
		val half = iv / 2
		new BoundingBox(lower - half, lower + half)
	}

	def translate(x: Double, y: Double, z: Double): BoundingBox = {
		translate(new Vec3d(x, y, z))
	}

	def translate(tv: Vec3d): BoundingBox = {
		new BoundingBox(lower + tv, upper + tv)
	}

	def center: Vec3d = (lower + upper) / 2

	def width: Double = upper.x - lower.x
	def height: Double = upper.y - lower.y
	def depth: Double = upper.z - lower.z

	def contains(point: Vec3d): Boolean = {
		point.between(lower, upper)
	}

	def contains(bb: BoundingBox): Boolean = {
		contains(bb.lower) && contains(bb.upper)
	}

	def intersectsBox(bb: BoundingBox): Boolean = {
		upper.checkEach(bb.lower, _ >= _) && lower.checkEach(bb.upper, _ <= _)
	}

	def intersectsSegment(a: Vec3d, b: Vec3d): Boolean = {
		val dir = b - a
		intersectsRay(a, dir, 0, 1)
	}

	def intersectsRay(point: Vec3d, dir: Vec3d, lambdaMin: Double, lambdaMax: Double): Boolean = {
		rayIntersection(point, dir, lambdaMin, lambdaMax).isDefined
	}

	def boxIntersection(bb: BoundingBox): BoundingBox = {
		new BoundingBox(Vec3d.max(lower, bb.lower), Vec3d.min(upper, bb.upper))
	}

	def sweptBoxIntersection(bb: BoundingBox, velocity: Vec3d): Option[RayBoxIntersection] = {
		val augmentedBox = inflate(bb.width, bb.height, bb.depth)
		augmentedBox.rayIntersection(bb.center, velocity, 0, Double.MaxValue)
		/* The intersection points represent the center of the colliding box at the collision
		   instant. The face is the one of this box, the face of the colliding box is the
		   opposite one. */
	}

	def sweptBoxIntersection(bb: BoundingBox, posA: Vec3d, posB: Vec3d): Option[RayBoxIntersection] = {
		val augmentedBox = inflate(bb.width, bb.height, bb.depth)
		augmentedBox.segmentIntersection(posA, posB)
		/* The intersection points represent the center of the colliding box at the collision
		   instant. The face is the one of this box, the face of the colliding box is the
		   opposite one. */
	}

	def segmentIntersection(a: Vec3d, b: Vec3d): Option[RayBoxIntersection] = {
		rayIntersection(a, b - a, 0, 1)
	}

	def rayIntersection(point: Vec3d, dir: Vec3d,
						lambdaMin: Double, lambdaMax: Double): Option[RayBoxIntersection] = {
		val (dx, dy, dz) = (dir.x, dir.y, dir.z)
		val (px, py, pz) = (point.x, point.y, point.z)
		val (invx, invy, invz) = (1 / dx, 1 / dy, 1 / dz)
		/* The ray is defined by the points (x,y,z) = point + lambda * dir
		Computes the lambdas for the intersections between the ray and each face of the box. */
		val (l1x, l2x) =
			if (dx < 0) {
				((upper.x - px) * invx, (lower.x - px) * invx)
			} else {
				((lower.x - px) * invx, (upper.x - px) * invx)
			}
		val (l1y, l2y) =
			if (dy < 0) {
				((upper.y - py) * invy, (lower.y - py) * invy)
			} else {
				((lower.y - py) * invy, (upper.y - py) * invy)
			}
		if (l1x > l2y || l1y > l2x) {
			return None // no intersection
		}
		val (l1z, l2z) =
			if (dz < 0) {
				((upper.z - pz) * invz, (lower.z - pz) * invz)
			} else {
				((lower.z - pz) * invz, (upper.z - pz) * invz)
			}
		if (l1x > l2z || l1z > l2x) {
			return None // no intersection
		}
		// Gets the final lambdas for the two intersection points I1 and I2
		val lambda1 = Math.max(l1x, Math.max(l1y, l1z)) // lambda of I1
		val lambda2 = Math.min(l2x, Math.min(l2y, l2z)) // lambda of I2
		/* Gets the face of I1 and I2. The face is null if and only if the intersection occurs
		outside of the specified interval [lambdaMin; lambdaMax] */
		val face1: Option[BlockFace] = {
			if (lambdaMin <= lambda1 && lambda1 <= lambdaMax) {
				if (lambda1 == l1x) {
					if (dx < 0) Some(BlockFace.EAST) else Some(BlockFace.WEST)
				} else if (lambda1 == l1y) {
					if (dy < 0) Some(BlockFace.UP) else Some(BlockFace.DOWN)
				} else {
					if (dz < 0) Some(BlockFace.SOUTH) else Some(BlockFace.NORTH)
				}
			} else {
				None // intersection I1 outside of the interval
			}
		}
		val face2: Option[BlockFace] = {
			if (lambdaMin <= lambda2 && lambda2 <= lambdaMax) {
				if (lambda1 == l1x) {
					if (dx >= 0) Some(BlockFace.EAST) else Some(BlockFace.WEST)
				} else if (lambda1 == l1y) {
					if (dy >= 0) Some(BlockFace.UP) else Some(BlockFace.DOWN)
				} else {
					if (dz >= 0) Some(BlockFace.SOUTH) else Some(BlockFace.NORTH)
				}
			} else {
				None // intersection I2 outside of the interval
			}
		}
		Some(new RayBoxIntersection(lambda1, lambda2, face1, face2, point, dir))
	}
}