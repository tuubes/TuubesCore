package org.tuubes.core.engine

/**
 * @author TheElectronWill
 */
final case class ChangeGroupMessage(val newGroup: ExecutionGroup) extends CoreMessage {}