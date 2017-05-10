package org.mcphoton.impl.network;

import com.github.steveice10.mc.protocol.ServerLoginHandler;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.game.setting.Difficulty;
import com.github.steveice10.mc.protocol.data.game.world.WorldType;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.packetlib.Session;
import org.mcphoton.Photon;

/**
 * Handles the login of a Player.
 *
 * @author TheElectronWill
 */
public class LoginHandlerImpl implements ServerLoginHandler {
	@Override
	public void loggedIn(Session session) {
		session.send(
				new ServerJoinGamePacket(0, false, GameMode.SURVIVAL, 0, Difficulty.PEACEFUL, 10,
										 WorldType.DEFAULT, false));
		session.send(new ServerChatPacket(
				"Welcome to the Photon server! Version " + Photon.getServer().getVersion()));
	}
}