package org.tuubes.messaging

import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.json.JsonFormat

/**
 * @author TheElectronWill
 */
final class TextComponent private(val text: String, c: Config) extends ChatComponent(c) {
	def this(text: String) = this(text, Config.of(JsonFormat.minimalInstance()))

	data.set("text", text)

	override def clone(): TextComponent = {
		new TextComponent(text, Config.copy(data))
	}

	override protected def appendTermData(sb: StringBuilder): Unit = {
		sb.append(text)
	}
}
object TextComponent {
	implicit def stringToComponent(str: String): TextComponent = new TextComponent(str)
}