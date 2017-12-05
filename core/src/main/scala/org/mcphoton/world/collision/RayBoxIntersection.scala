package org.mcphoton.world.collision

import com.electronwill.utils.Vec3d
import org.mcphoton.block.BlockFace

/**
 * @author TheElectronWill
 */
final class RayBoxIntersection(val lambda1: Double, val lambda2: Double,
							   val face1: Option[BlockFace], val face2: Option[BlockFace],
							   val rayPoint: Vec3d, val rayDir: Vec3d) {
	def firstIntersectionPoint: Vec3d = {
		rayDir * lambda1 + rayPoint
	}

	def secondIntersectionPoint: Vec3d = {
		rayDir * lambda2 + rayPoint
	}
	def intersectionVector: Vec3d = {
		rayDir * (lambda2 - lambda1)
	}
}