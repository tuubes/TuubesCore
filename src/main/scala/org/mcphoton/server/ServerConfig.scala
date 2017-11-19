package org.mcphoton.server

import java.awt.image.BufferedImage
import java.io.IOException
import java.util
import javax.imageio.ImageIO

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig
import com.electronwill.nightconfig.core.conversion.{Conversion, Converter, ObjectConverter, SpecIntInRange}
import com.electronwill.nightconfig.core.file.CommentedFileConfig
import com.electronwill.utils.StringUtils
import org.mcphoton.server.ServerConfig.{LocationConverter, LogLevelConverter}
import org.mcphoton.world.{Location, World, WorldType}

/**
 * @author TheElectronWill
 */
final class ServerConfig {
	@transient
	private val file = PhotonServer.DirConfig / "server_config.toml"

	@transient
	@volatile
	private var savedComments: java.util.Map[String, UnmodifiableCommentedConfig.CommentNode] = _

	@volatile var motd = "Default MOTD. Change it in the config."
	@volatile var maxPlayers = 10
	@volatile var port = 25565
	@volatile var onlineMode = false

	@Conversion(classOf[LocationConverter])
	@volatile var spawnLocation: Location = _

	@Conversion(classOf[LogLevelConverter])
	@volatile var logLevel = LogLevel.TRACE

	@SpecIntInRange(min = 0, max = 1000)
	@volatile var threadNumber = Runtime.getRuntime.availableProcessors()

	@transient
	@volatile var icon: Option[BufferedImage] = None

	def load(): Unit = {
		val conf = CommentedFileConfig.builder(file.toJava)
				   .defaultResource("/default-config.toml").build()
		conf.load()
		new ObjectConverter().toObject(conf, this)
		savedComments = conf.getComments
		icon = readIcon()
	}

	private def readIcon(): Option[BufferedImage] = {
		val possibilities = Array("icon.png", "icon.jpg", "favicon.png", "favicon.jpg",
			"server-icon.png", "server-icon.jpg", "server_icon.png", "server_icon.jpg",
			"logo.png", "logo.jpg")
		for (possibility <- possibilities) {
			val file = (PhotonServer.DirMain / possibility).toJava
			if (file.exists) {
				try
					return Some(ImageIO.read(file))
				catch {
					case e: IOException =>
						throw new RuntimeException("Unable to read the logo from " + file, e)
				}
			}
		}
		None
	}
}
object ServerConfig {
	private class LogLevelConverter extends Converter[LogLevel, String] {
		override def convertToField(value: String): LogLevel = LogLevel.valueOf(value.toUpperCase)
		override def convertFromField(value: LogLevel): String = value.name
	}

	private class LocationConverter extends Converter[Location, String] {
		override def convertToField(v: String): Location = {
			var value = v
			// Remove leading ( if any:
			if (value.charAt(0) == '(') {
				value = value.substring(1)
			}
			// Remove trailing ) if any:
			if (value.charAt(value.length - 1) == ')') {
				value = value.substring(0, value.length - 1)
			}
			// Split parts:
			val parts: util.List[String] = StringUtils.split(value, ',')
			val x: Double = parts.get(0).trim.toDouble
			val y: Double = parts.get(1).trim.toDouble
			val z: Double = parts.get(2).trim.toDouble
			val worldName: String = parts.get(3).trim
			val world = PhotonServer.world(worldName) match {
				case None =>
					val w = new World(worldName, WorldType.OVERWORLD)
					PhotonServer.registerWorld(w)
					w
				case Some(w) => w
			}
			Location(x, y, z, world)
		}

		override def convertFromField(value: Location): String = {
			s"${value.x},${value.y},${value.z},${value.world.name}"
		}
	}
}