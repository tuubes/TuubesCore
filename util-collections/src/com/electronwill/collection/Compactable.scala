package com.electronwill.collection

/**
 * Trait for collections that can be "compacted", that is, reduce their in-memory size to the strict
 * minimum required to store their elements.
 *
 * @author TheElectronWill
 */
trait Compactable {
  /**
   * Compacts this object to minimize its memory use.
   */
  def compact(): Unit
}
