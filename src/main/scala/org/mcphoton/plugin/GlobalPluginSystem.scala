package org.mcphoton.plugin

import org.mcphoton.plugin.PluginState._

/**
 * @author TheElectronWill
 */
object GlobalPluginSystem extends PluginSystem[GlobalPlugin] {
	override def enable(p: GlobalPlugin): Unit = {
		if (p.state == LOADED) {
			p.onGlobalEnable()
			plugins.put(p.name, p)
			p.state = ENABLED
		} // else the plugin is already enabled or disabled and thus cannot be enabled
	}

	override def disable(p: GlobalPlugin): Unit = {
		if (p.state == ENABLED) {
			for (world <- p.worlds) {
				world.pluginSystem.disable(p)
			}
			p.onGlobalDisable()
			plugins.remove(p.name)
			p.state = DISABLED
		} // else the plugin isn't enabled and thus cannot be disabled
	}
}