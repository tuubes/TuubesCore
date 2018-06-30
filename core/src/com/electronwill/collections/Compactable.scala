package com.electronwill.collections

/**
 * @author TheElectronWill
 */
trait Compactable {

  /**
	 * Compacts this object to minimize its memory use.
	 */
  def compact(): Unit
}
