package org.mcphoton.server

import java.net.{InetAddress, UnknownHostException}
import java.util.concurrent.ConcurrentSkipListSet
import java.util.{Collections, UUID, Collection => JSeq}

import com.electronwill.nightconfig.core.ConfigSpec
import com.electronwill.nightconfig.core.file.FileConfig
import com.typesafe.scalalogging.StrictLogging
import scala.collection.JavaConverters._

/**
 * @author TheElectronWill
 */
object AccessController extends StrictLogging {
	private val ConfigSpec = new ConfigSpec
	ConfigSpec.define("accounts", Collections.emptyList())
	ConfigSpec.define("addresses", Collections.emptyList())

	private val File = PhotonServer.DirConfig / "access_control.toml"
	private val Config = FileConfig.builder(File.toJava)
						 .defaultResource("/default-ac.toml")
						 .build()
	private val accountSet = new ConcurrentSkipListSet[UUID]
	private val addressSet = new ConcurrentSkipListSet[InetAddress]

	def load(): Unit = {
		Config.load()
		val nCorrections = ConfigSpec.correct(Config)
		if (nCorrections > 0) {
			println(s"Corrected $nCorrections entries in ${File.name}")
		}
		for (accountString <- Config.get[JSeq[String]]("accounts").asScala) {
			try {
				accountSet.add(UUID.fromString(accountString))
			} catch {
				case _: IllegalArgumentException =>
					logger.warn(s"Invalid account id $accountString")
			}
		}
		for (addressString <- Config.get[JSeq[String]]("addresses").asScala) {
			try {
				addressSet.add(InetAddress.getByName(addressString))
			} catch {
				case _: IllegalArgumentException | _: UnknownHostException =>
					logger.warn(s"Invalid address $addressString")
			}
		}
	}

	def save(): Unit = {
		Config.set("accounts", accountSet)
		Config.set("addresses", addressSet)
		Config.save()
	}

	def isBlocked(accountId: UUID): Boolean = accountSet.contains(accountId)
	def isBlocked(address: InetAddress): Boolean = addressSet.contains(address)

	def allow(accountId: UUID): Unit = accountSet.remove(accountId)
	def allow(address: InetAddress): Unit = addressSet.remove(address)

	def block(accountId: UUID): Unit = accountSet.add(accountId)
	def block(address: InetAddress): Unit = addressSet.add(address)
}