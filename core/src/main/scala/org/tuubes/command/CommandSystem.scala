package org.tuubes.command

import java.util.concurrent.ConcurrentHashMap

import org.tuubes.world.World

import scala.collection.concurrent
import scala.collection.JavaConverters._

/**
 * @author TheElectronWill
 */
class CommandSystem {
	/**
	 * Registers a command to this CommandSystem.
	 *
	 * @param cmd the command to register
	 */
	def register(cmd: Command): Unit = commands.put(cmd.name, cmd)

	/** Unregisters a command from this CommandSystem.
	 *
	 * @param cmd the command to unregister
	 * @return true if the command has been unregistered, false if it wasn't registered
	 */
	def unregister(cmd: Command): Boolean = commands.remove(cmd.name).isDefined

	/** @return the command with the given name */
	def get(name: String): Option[Command] = commands.get(name)

	/**
	 * Defines an alias that executes a command. The command may have arguments, as if it was
	 * typed in the chat.
	 *
	 * @param alias the alias' name
	 * @param cmd   the commands to execute
	 */
	def setAlias(alias: String, cmd: String): Unit = aliases.put(alias, cmd)
	//TODO support expanding arguments in alias definition, eg setAlias("a", "cmd $arg0")

	/**
	 * Gets the command associated with an alias.
	 *
	 * @param alias the alias
	 * @return the command associated to the alias, or None
	 */
	def getAlias(alias: String): Option[String] = aliases.get(alias)

	private[this] val commands: concurrent.Map[String, Command] = new ConcurrentHashMap().asScala
	private[this] val aliases: concurrent.Map[String, String] = new ConcurrentHashMap().asScala
}
object CommandSystem {
	def apply(implicit w: World): CommandSystem = w.commandSystem

	val global = new CommandSystem()
}