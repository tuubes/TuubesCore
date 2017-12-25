package org.tuubes.entity.objects

import org.tuubes.entity.{BasicEntity, ObjectType}

/**
 * @author TheElectronWill
 */
abstract class ObjectEntity(t: ObjectType, private[tuubes] val objectData: Int) extends BasicEntity(t) {

}