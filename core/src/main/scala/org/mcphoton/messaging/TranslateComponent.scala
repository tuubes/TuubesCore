package org.mcphoton.messaging

import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.json.JsonFormat
import java.{util => ju}

/**
 * @author TheElectronWill
 */
final class TranslateComponent private(val translationString: String,
									   val replacements: ju.List[ChatComponent],
									   c: Config) extends ChatComponent(c) {

	def this(translationString: String) = {
		this(translationString, new ju.ArrayList[ChatComponent](2), Config.of(JsonFormat.minimalInstance))
	}

	def replacements(r: ChatComponent*): this.type = {
		r.foreach(replacements.add)
		this
	}

	def addReplacement(r: ChatComponent): this.type = {
		replacements.add(r)
		this
	}

	data.set("translate", translationString)
	data.set("with", replacements)

	override def clone(): TranslateComponent = {
		new TranslateComponent(translationString, new ju.ArrayList(replacements), Config.copy(data))
	}

	override protected def appendTermData(sb: StringBuilder): Unit = {
		sb.append("$translate(").append(translationString).append(')')
		sb.append(".with(").append(ju.Arrays.asList(replacements.toArray)).append(')')
	}
}