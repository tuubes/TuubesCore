package org.mcphoton.entity.objects

import org.mcphoton.entity.{BasicEntity, ObjectType}

/**
 * @author TheElectronWill
 */
abstract class ObjectEntity(t: ObjectType, private[mcphoton] val objectData: Int) extends BasicEntity(t) {

}