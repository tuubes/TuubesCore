package com.electronwill.collection

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Represents the registration of an element to an [[Index]].
 *
 * @author TheElectronWill
 */
final class IndexRegistration(private[this] val i: Index[_], private[this] val id: Int) {
  private[this] val valid = new AtomicBoolean(true)

  def cancel(): Unit = {
    if (valid.compareAndSet(true, false)) {
      i -= id
    }
  }

  def isValid: Boolean = valid.get()
}
