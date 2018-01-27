package org.tuubes.core.blocks

import com.electronwill.collections.RecyclingIndex
import org.tuubes.core.Type

import scala.collection.mutable

/**
 * A type of block.
 *
 * @author TheElectronWill
 */
abstract class BlockType(n: String, id: Int) extends Type[BlockType](n) {
	private[tuubes] val internalId = id
}
object BlockType {
	private val ids = new RecyclingIndex[BlockType]()
	private val names = new mutable.AnyRefMap[String, BlockType]()

	def apply(uniqueName: String): Option[BlockType] = {
		names.get(uniqueName)
	}

	private[tuubes] def apply(internalId: Int): Option[BlockType] = {
		ids(internalId)
	}

	private[tuubes] def register(t: BlockType): Unit = {
		ids(t.internalId) = t
		names(t.uniqueName) = t
	}
}