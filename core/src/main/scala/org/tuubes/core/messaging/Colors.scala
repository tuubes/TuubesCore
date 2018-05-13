package org.tuubes.messaging

/**
 * @author TheElectronWill
 */
object Colors extends Enumeration {
  final case class V protected (name: String, code: String, termCode: String)
      extends super.Val(name) {}
  type Color = V
  val Black = V("black", "§0", "\u001B[2m\u001B[30m")
  val Blue = V("blue", "§9", "\u001B[34m")
  val Aqua = V("aqua", "§b", "\u001B[36m")
  val DarkBlue = V("dark_blue", "§1", "\u001B[2m\u001B[34m")
  val DarkAqua = V("dark_aqua", "§3", "\u001B[2m\u001B[36m")
  val DarkGreen = V("dark_green", "§2", "\u001B[2m\u001B[32m")
  val DarkGrey = V("dark_grey", "§8", "\u001B[30m")
  val Purple = V("dark_purple", "§5", "\u001B[2m\u001B[35m")
  val DarkRed = V("dark_red", "§4", "\u001B[2m\u001B[31m")
  val Gold = V("gold", "§6", "\u001B[2m\u001B[33m")
  val Green = V("green", "§a", "\u001B[32m")
  val Grey = V("grey", "§7", "\u001B[37m")
  val Pink = V("light_purple", "§d", "\u001B[35m")
  val Red = V("red", "§c", "\u001B[31m")
  val White = V("white", "§f", "\u001B[37m")
  val Yellow = V("yellow", "§e", "\u001B[33m")
  val Reset = V("reset", "§r", "\u001B[0m")
}
