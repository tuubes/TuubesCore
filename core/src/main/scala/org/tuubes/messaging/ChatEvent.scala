package org.tuubes.messaging

import java.util

import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.json.JsonFormat

/**
 * @author TheElectronWill
 */
abstract class ChatEvent(val action: String, val value: String) {
	def toConfig: Config = {
		val map = new util.HashMap[String, Object](2)
		map.put("action", action)
		map.put("value", value)
		Config.wrap(map, JsonFormat.minimalInstance())
	}
}