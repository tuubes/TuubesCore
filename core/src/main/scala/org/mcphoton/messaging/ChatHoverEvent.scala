package org.mcphoton.messaging

/**
 * @author TheElectronWill
 */
abstract sealed class ChatHoverEvent(a: String, v: String) extends ChatEvent(a, v) {}
final case class ShowText(txt: String) extends ChatHoverEvent("show_text", txt)
final case class ShowItem(itemJson: String) extends ChatHoverEvent("show_item", itemJson)
final case class ShowEntity(entityJson: String) extends ChatHoverEvent("show_entity", entityJson)