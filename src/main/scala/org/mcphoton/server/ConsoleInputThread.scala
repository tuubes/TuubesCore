package org.mcphoton.server

import java.{util => ju}

import better.files.Scanner
import com.electronwill.utils.StringUtils
import org.mcphoton.command.{Command, CommandSystem}
import org.mcphoton.messaging.{ChatMessage, Messageable}

import scala.collection.JavaConverters._

/**
 * @author TheElectronWill
 */
object ConsoleInputThread extends Thread("console") with Messageable {
	@volatile private var _run = true
	private val sc = Scanner(System.in)

	def stopNicely(): Unit = _run = false

	override def sendMessage(msg: CharSequence): Unit = println(msg)
	override def sendMessage(msg: ChatMessage): Unit = println(msg.toConsoleString)

	override def run(): Unit = {
		val parts = new ju.ArrayList[String]
		while (_run) {
			val line = sc.nextLine()
			StringUtils.splitArguments(line, parts)
			val cmdName = parts.get(0)
			val cmd = CommandSystem.global.get(cmdName)
			cmd match {
				case None => println(s"Unknown command: $cmdName")
				case Some(c: Command) =>
					val args = parts.subList(1, parts.size).asScala
					c.execute(this, args)(null, null)
			}
		}
	}
}