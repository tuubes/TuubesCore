package org.tuubes.messaging

import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.json.JsonFormat

/**
 * @author TheElectronWill
 */
final class KeybindComponent private(val keybind: String, c: Config) extends ChatComponent(c) {
	def this(keybind: String) = this(keybind, Config.of(JsonFormat.minimalInstance()))

	data.set("keybind", keybind)

	override def clone(): KeybindComponent = {
		new KeybindComponent(keybind, Config.copy(data))
	}
	override protected def appendTermData(sb: StringBuilder): Unit = {
		sb.append("$keybind(").append(keybind).append(')')
	}
}