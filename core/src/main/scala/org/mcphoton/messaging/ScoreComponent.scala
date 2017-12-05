package org.mcphoton.messaging

import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.json.JsonFormat

/**
 * @author TheElectronWill
 */
final class ScoreComponent private(val name: String, val objective: String,
								   val value: String, c: Config) extends ChatComponent(c) {
	def this(name: String, objective: String, value: String) = {
		this(name, objective, value, Config.of(JsonFormat.minimalInstance))
	}

	{
		val scoreConfig = Config.inMemory()
		scoreConfig.set("name", name)
		scoreConfig.set("objective", objective)
		scoreConfig.set("value", value)
		data.set("score", scoreConfig)
	}

	override def clone(): ScoreComponent = {
		new ScoreComponent(name, objective, value, Config.copy(data))
	}
}