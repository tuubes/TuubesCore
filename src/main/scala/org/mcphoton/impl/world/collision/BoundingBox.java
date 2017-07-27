package org.mcphoton.impl.world.collision;

import java.util.Objects;
import org.mcphoton.block.BlockFace;
import org.mcphoton.utils.Coordinates;
import org.mcphoton.utils.Vector;

/**
 * @author TheElectronWill
 */
public final class BoundingBox implements Cloneable {
	private final Vector lower, upper;

	public BoundingBox(Vector lower, Vector upper) {
		this.lower = lower.clone();
		this.upper = upper.clone();
	}

	public BoundingBox(Coordinates center, double width, double height, double depth) {
		final double halfWidth = width / 2.0, halfHeight = height / 2.0, halfDepth = depth / 2.0;
		this.lower = new Vector(center.getX() - halfWidth, center.getY() - halfHeight,
								center.getZ() - halfDepth);
		this.upper = new Vector(center.getX() + halfWidth, center.getY() + halfHeight,
								center.getZ() + halfDepth);
	}

	public BoundingBox inflated(double padX, double padY, double padZ) {
		Vector half = new Vector(padX / 2.0, padY / 2.0, padZ / 2.0);
		Vector l = lower.clone().sub(half);
		Vector u = upper.clone().add(half);
		return new BoundingBox(l, u);
	}

	public void translate(Vector translation) {
		lower.add(translation);
		upper.add(translation);
	}

	public Coordinates getCenter() {
		return new Vector(lower).add(upper).multiply(0.5);
	}

	public double getWidth() {
		return upper.getX() - lower.getX();
	}

	public double getHeight() {
		return upper.getY() - lower.getY();
	}

	public double getDepth() {
		return upper.getZ() - lower.getZ();
	}

	public boolean contains(Coordinates c) {
		return contains(c.getX(), c.getY(), c.getZ());
	}

	public boolean contains(double x, double y, double z) {
		return x > lower.getX()
			   && x < upper.getX()
			   && y > lower.getY()
			   && y < upper.getY()
			   && z > lower.getZ()
			   && z < upper.getZ();
	}

	public boolean contains(BoundingBox box) {
		return contains(box.lower) && contains(box.upper);
	}

	public boolean intersectsBox(BoundingBox box) {
		return upper.isBiggerThan(box.lower) && lower.isSmallerThan(box.upper);
	}

	public boolean intersectsSegment(Coordinates a, Coordinates b) {
		Vector dir = new Vector(b).sub(a);
		return intersectsRay(a, dir, 0.0, 1.0);
	}

	public boolean intersectsRay(Coordinates point, Vector dir, double lambdaMin,
								 double lambdaMax) {
		return intersectionWithRay(point, dir, lambdaMin, lambdaMax) != null;
	}

	public BoundingBox intersectionWithBox(BoundingBox box) {
		Vector lower = Vector.max(this.lower, box.lower);
		Vector upper = Vector.min(this.upper, box.upper);
		return new BoundingBox(lower, upper);
	}

	public RayBoxIntersection sweptIntersectionWithBox(BoundingBox box, Vector velocity) {
		Coordinates center = box.getCenter();
		BoundingBox augmentedBox = this.inflated(box.getWidth(), box.getHeight(), box.getDepth());
		return augmentedBox.intersectionWithRay(center, velocity, 0, Double.POSITIVE_INFINITY);
		// The intersection points represent the center of the colliding box at the collision
		// instant. The face is the one of this box, the face of the colliding box is the
		// opposite one.
	}

	public RayBoxIntersection sweptIntersectionWithBox(BoundingBox box, Vector posA, Vector posB) {
		BoundingBox augmentedBox = this.inflated(box.getWidth(), box.getHeight(), box.getDepth());
		return augmentedBox.intersectionWithSegment(posA, posB);
		// The intersection points represent the center of the colliding box at the collision
		// instant. The face is the one of this box, the face of the colliding box is the
		// opposite one.
	}

	public RayBoxIntersection intersectionWithSegment(Coordinates a, Coordinates b) {
		Vector dir = new Vector(b).sub(a);
		return intersectionWithRay(a, dir, 0.0, 1.0);
	}

	public RayBoxIntersection intersectionWithRay(Coordinates rayPoint, Vector rayDir,
												  double lambdaMin, double lambdaMax) {
		double l1x, l1y, l1z;
		double l2x, l2y, l2z;
		double invDx = 1 / rayDir.getX(), invDy = 1 / rayDir.getY(), invDz = 1 / rayDir.getZ();
		// The ray is defined by the points (x,y,z) = rayPoint + lambda * rayDir
		// Computes the lambdas for the intersections between the ray and each face of the box.
		if (rayDir.getX() < 0) {
			l1x = (upper.getX() - rayPoint.getX()) * invDx;
			l2x = (lower.getX() - rayPoint.getX()) * invDx;
		} else {
			l2x = (lower.getX() - rayPoint.getX()) * invDx;
			l1x = (upper.getX() - rayPoint.getX()) * invDx;
		}
		if (rayDir.getY() < 0) {
			l1y = (upper.getY() - rayPoint.getY()) * invDy;
			l2y = (lower.getY() - rayPoint.getY()) * invDy;
		} else {
			l2y = (lower.getY() - rayPoint.getY()) * invDy;
			l1y = (upper.getY() - rayPoint.getY()) * invDy;
		}
		if (l1x > l2y || l1y > l2x) {
			return null;//no intersection
		}
		if (rayDir.getZ() < 0) {
			l1z = (upper.getZ() - rayPoint.getZ()) * invDz;
			l2z = (lower.getZ() - rayPoint.getZ()) * invDz;
		} else {
			l2z = (lower.getZ() - rayPoint.getZ()) * invDz;
			l1z = (upper.getZ() - rayPoint.getZ()) * invDz;
		}
		if (l1x > l2z || l1z > l2x) {
			return null;//no intersection
		}
		// Gets the final lambda for the two intersection points I1 and I2
		double lambda1 = Math.max(l1x, Math.max(l1y, l1z));
		double lambda2 = Math.min(l2x, Math.min(l2y, l2z));
		// Gets the face of I1 and I2
		// The face is null iff the intersection occurs out of the interval [lambdaMin, lambdaMax]
		BlockFace face1 = null, face2 = null;
		if (lambdaMin <= lambda1 && lambda1 <= lambdaMax) {
			// Gets the face of I1
			if (lambda1 == l1x) {
				face1 = (rayDir.getX() < 0) ? BlockFace.EAST : BlockFace.WEST;
			} else if (lambda1 == l1y) {
				face1 = (rayDir.getY() < 0) ? BlockFace.UP : BlockFace.DOWN;
			} else {//lambda1 == l1z
				face1 = (rayDir.getZ() < 0) ? BlockFace.SOUTH : BlockFace.NORTH;
			}
		}
		if (lambdaMin <= lambda2 && lambda2 <= lambdaMax) {
			// Gets the face of I2
			if (lambda2 == l2x) {
				face2 = (rayDir.getX() > 0) ? BlockFace.EAST : BlockFace.WEST;
			} else if (lambda2 == l2y) {
				face2 = (rayDir.getY() > 0) ? BlockFace.UP : BlockFace.DOWN;
			} else {//lambda2 == l2z
				face2 = (rayDir.getZ() > 0) ? BlockFace.SOUTH : BlockFace.NORTH;
			}
		}
		// Returns the result
		return new RayBoxIntersection(lambda1, lambda2, face1, face2, rayPoint, rayDir);
	}

	public static final class RayBoxIntersection {
		private final double lambda1, lambda2;
		private final BlockFace face1, face2;
		private final Coordinates rayPoint;
		private final Vector rayDir;

		RayBoxIntersection(double lambda1, double lambda2, BlockFace face1, BlockFace face2,
						   Coordinates rayPoint, Vector rayDir) {
			this.lambda1 = lambda1;
			this.lambda2 = lambda2;
			this.face1 = face1;
			this.face2 = face2;
			this.rayPoint = rayPoint;
			this.rayDir = rayDir;
		}

		public double getLambda1() {
			return lambda1;
		}

		public double getLambda2() {
			return lambda2;
		}

		public BlockFace getFirstIntersectionFace() {
			return face1;
		}

		public BlockFace getSecondIntersectionFace() {
			return face2;
		}

		public Vector getFirstIntersectionPoint() {
			if (face1 == null) { return null; }
			return new Vector(rayDir).multiply(lambda1).add(rayPoint);
		}

		public Vector getSecondIntersectionPoint() {
			if (face2 == null) { return null; }
			return new Vector(rayDir).multiply(lambda2).add(rayPoint);
		}

		public Vector getIntersectionVector() {
			return new Vector(rayDir).multiply(lambda2 - lambda1);
		}
	}

	@Override
	public BoundingBox clone() {
		return new BoundingBox(lower.clone(), upper.clone());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) { return true; }
		if (!(o instanceof BoundingBox)) { return false; }
		BoundingBox that = (BoundingBox)o;
		return Objects.equals(lower, that.lower) && Objects.equals(upper, that.upper);
	}

	@Override
	public int hashCode() {
		return Objects.hash(lower, upper);
	}
}