package org.tuubes.messaging

/**
 * @author TheElectronWill
 */
abstract sealed class ChatClickEvent(a: String, v: String) extends ChatEvent(a, v) {}
final case class OpenUrl(url: String) extends ChatClickEvent("open_url", url)
final case class Say(msg: String) extends ChatClickEvent("run_command", msg)
final case class Suggest(msg: String) extends ChatClickEvent("suggest_command", msg)
final case class ChangePage(nPage: Int) extends ChatClickEvent("change_page", nPage.toString)