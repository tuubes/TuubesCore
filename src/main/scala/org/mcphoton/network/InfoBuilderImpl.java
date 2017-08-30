package org.mcphoton.network;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.data.message.TextMessage;
import com.github.steveice10.mc.protocol.data.status.PlayerInfo;
import com.github.steveice10.mc.protocol.data.status.ServerStatusInfo;
import com.github.steveice10.mc.protocol.data.status.VersionInfo;
import com.github.steveice10.mc.protocol.data.status.handler.ServerInfoBuilder;
import com.github.steveice10.packetlib.Session;
import java.awt.image.BufferedImage;
import org.mcphoton.Photon;
import org.mcphoton.server.Server;

/**
 * Builds {@link ServerStatusInfo} object to answer to the status requests sent by the clients.
 *
 * @author TheElectronWill
 */
public class InfoBuilderImpl implements ServerInfoBuilder {
	private static final GameProfile[] EMPTY_PROFILE_ARRAY = {};
	private static final VersionInfo VERSION_INFO = new VersionInfo(Photon.getMinecraftVersion(),
																	MinecraftConstants.PROTOCOL_VERSION);

	@Override
	public ServerStatusInfo buildInfo(Session session) {
		Server server = Photon.getServer();
		PlayerInfo playerInfo = new PlayerInfo(server.getConfiguration().getMaxPlayers(),
											   server.getOnlinePlayers().size(),
											   EMPTY_PROFILE_ARRAY);
		Message message = new TextMessage(server.getConfiguration().getMOTD());
		BufferedImage icon = server.getConfiguration().getIcon();
		return new ServerStatusInfo(VERSION_INFO, playerInfo, message, icon);
	}
}