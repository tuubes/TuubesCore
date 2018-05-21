package org.tuubes.core.engine.messages

import org.tuubes.core.worlds.LocalWorld

/**
 * @author TheElectronWill
 */
final case class MoveToWorld(newWorld: LocalWorld) extends EngineMessage {}
