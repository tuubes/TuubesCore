package org.tuubes.messaging

import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.json.JsonFormat

/**
 * @author TheElectronWill
 */
final class SelectorComponent private(val selector: String, c: Config) extends ChatComponent(c) {
	def this(selector: String) = this(selector, Config.of(JsonFormat.minimalInstance()))

	data.set("selector", selector)

	override def clone(): SelectorComponent = {
		new SelectorComponent(selector, Config.copy(data))
	}

	override protected def appendTermData(sb: StringBuilder): Unit = {
		sb.append("$selector(").append(selector).append(')')
	}
}