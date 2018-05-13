package org.tuubes.core.tasks

import java.util.concurrent.TimeUnit

/**
 * @author TheElectronWill
 */
trait DelayedTask extends CancellableTask {
  def getDelay(unit: TimeUnit): Long
}
