package org.mcphoton.network

import java.awt.image.BufferedImage

import com.github.steveice10.mc.auth.data.GameProfile
import com.github.steveice10.mc.protocol.MinecraftConstants
import com.github.steveice10.mc.protocol.data.message.{Message, TextMessage}
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoBuilder
import com.github.steveice10.mc.protocol.data.status.{PlayerInfo, ServerStatusInfo, VersionInfo}
import com.github.steveice10.packetlib.Session
import org.mcphoton.server.PhotonServer

/**
 * Builds [[ServerStatusInfo]] objects to answer to the status requests sent by the clients.
 *
 * @author TheElectronWill
 */
final class InfoBuilder extends ServerInfoBuilder {
	private val emptyProfileArray = new Array[GameProfile](0)
	private val versionInfo = new VersionInfo(PhotonServer.MinecraftVersion, MinecraftConstants.PROTOCOL_VERSION)

	@Override
	def buildInfo(session: Session): ServerStatusInfo = {
		val playerInfo: PlayerInfo = new PlayerInfo(PhotonServer.Config.maxPlayers,
			PhotonServer.onlinePlayers.size,
			emptyProfileArray)
		val message: Message = new TextMessage(PhotonServer.Config.motd)
		val icon: BufferedImage = PhotonServer.Config.icon.orNull
		new ServerStatusInfo(versionInfo, playerInfo, message, icon)
	}
}