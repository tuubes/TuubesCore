package org.mcphoton.messaging

/**
 * @author TheElectronWill
 */
abstract sealed class ChatHoverEvent(a: String, v: String) extends ChatEvent(a, v) {
	final class ShowText(txt: String) extends ChatHoverEvent("show_text", txt)
	final class ShowItem(itemJson: String) extends ChatHoverEvent("show_item", itemJson)
	final class ShowEntity(entityJson: String) extends ChatHoverEvent("show_entity", entityJson)
}