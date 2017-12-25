package org.tuubes.user

import java.util.UUID

import better.files.File
import com.electronwill.nightconfig.core.file.FileConfig
import com.electronwill.nightconfig.json.JsonParser
import com.electronwill.utils.StringUtils
import org.tuubes.entity.mobs.Player
import org.tuubes.server.PhotonServer
import org.tuubes.world.Location

import scala.collection.mutable

/**
 * @author TheElectronWill
 */
object UserManager {
	private[this] final val dataDir = PhotonServer.DirMain / "players"
	private[this] final val onlineUids = new mutable.HashMap[UUID, Player]
	private[this] final val onlineNames = new mutable.HashMap[String, Player]

	/** @return the player if currently online, or None */
	def getOnline(name: String): Option[Player] = onlineNames.get(name)

	/** @return the player if currently  online, or None */
	def getOnline(uid: UUID): Option[Player] = onlineUids.get(uid)

	/** @return the User if known by the server, or None */
	def get(name: String): Option[User] = {
		getOnline(name).orElse(getOffline(name))
	}

	/** @return the User if known by the server, or None */
	def get(uuid: UUID): Option[User] = {
		getOnline(uuid).orElse(getOffline(uuid))
	}

	def save(user: User): Unit = {
		val file = dataDir / (user.name + ":" + user.accountId + ".json")
		val config = FileConfig.builder(file.toJava).sync().build()
		config.set("location", user.location.toString)
		config.save()
	}

	private def getOffline(name: String): Option[User] = {
		val fileIterator = dataDir.glob(s"$name:*.json")
		if (fileIterator.hasNext) {
			val file = fileIterator.next()
			val idString = StringUtils.split(file.name, ':').get(1).replace(".json", "")
			val uuid = UUID.fromString(idString)
			Some(parseUserData(file, name, uuid))
		} else {
			None
		}
	}

	private def getOffline(uuid: UUID): Option[User] = {
		val fileIterator = dataDir.glob(s"*:$uuid.json")
		if (fileIterator.hasNext) {
			val file = fileIterator.next()
			val name = StringUtils.split(file.name, ':').get(0)
			Some(parseUserData(file, name, uuid))
		} else {
			None
		}
	}

	private def parseUserData(file: File, name: String, uuid: UUID): User = {
		for (reader <- file.fileReader) { // reader autoclosed
			val userData = new JsonParser().parse(reader)
			val location = Location.fromString(userData.get("location"))
			return new OfflineUser(name, uuid, location)
		}
		null
	}
}