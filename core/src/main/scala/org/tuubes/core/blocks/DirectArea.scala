package org.tuubes.core.blocks

import com.electronwill.utils.Vec3i
import org.tuubes.block.BlockType

/**
 * A directly accessible Area.
 *
 * @author TheElectronWill
 */
trait DirectArea extends Area {
	/**
	 * @return the total number of blocks (including air) in this area
	 */
	def size: Int

	/**
	 * @return the minimum coordinates included in the area
	 */
	def min: Vec3i

	/**
	 * @return the maximum coordinates included in the area
	 */
	def max: Vec3i

	def apply(x: Int, y: Int, z: Int): BlockType

	final def apply(p: Vec3i): BlockType = {
		apply(p.x, p.y, p.z)
	}

	def update(x: Int, y: Int, z: Int, t: BlockType): Unit

	final def update(p: Vec3i, t: BlockType): Unit = {
		update(p.x, p.y, p.z, t)
	}

	def replace(replace: BlockType, replacement: BlockType): Unit

	def replace(replace: Array[BlockType], replacements: Array[BlockType]): Unit

	def replace(from: Vec3i, to: Vec3i, replace: Array[BlockType], replacements: Array[BlockType]): Unit

	def fill(block: BlockType): Unit

	def fill(block: BlockType, exceptions: Array[BlockType]): Unit

	def fill(from: Vec3i, to: Vec3i, fillWith: BlockType, exceptions: Array[BlockType]): Unit
}