package org.tuubes.core.engine.messages

import org.tuubes.core.engine.ExecutionGroup

/**
 * @author TheElectronWill
 */
final case class MoveToGroup(newGroup: ExecutionGroup) extends EngineMessage {}
