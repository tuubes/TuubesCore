package org.mcphoton.block

import com.electronwill.collections.{ConcurrentRecyclingIndex, IndexRegistration}
import org.mcphoton.world.Location

/**
 * @author TheElectronWill
 */
final class BlockRef private[mcphoton](val blockLocation: Location,
									   private[this] var _blockType: BlockType) {
	def blockType: BlockType = _blockType

	private[this] val observers = new ConcurrentRecyclingIndex[(BlockType, BlockType) => Unit]

	def addObserver(f: (BlockType, BlockType) => Unit): IndexRegistration = {
		val id = observers += f
		new IndexRegistration(observers, id)
	}

	private[mcphoton] def notify(newType: BlockType): Unit = {
		val oldType = _blockType
		_blockType = newType
		observers.valuesIterator.foreach(_ (oldType, newType))
	}
}