package org.tuubes.core.engine

/**
 * @author TheElectronWill
 */
trait Registration[A] {
  def cancel(): Unit
  def isValid: Boolean
}
