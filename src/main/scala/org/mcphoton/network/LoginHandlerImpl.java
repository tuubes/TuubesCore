package org.mcphoton.network;

import com.github.steveice10.mc.protocol.ServerLoginHandler;
import com.github.steveice10.mc.protocol.data.game.entity.metadata.IntPosition;
import com.github.steveice10.mc.protocol.data.game.entity.player.GameMode;
import com.github.steveice10.mc.protocol.data.game.setting.Difficulty;
import com.github.steveice10.mc.protocol.data.game.world.WorldType;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerAbilitiesPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerSpawnPositionPacket;
import com.github.steveice10.packetlib.Session;
import org.mcphoton.runtime.TaskSystem;
import org.mcphoton.server.PhotonServer;
import org.mcphoton.world.ChunkColumn;
import org.mcphoton.world.ChunkColumnImpl;
import org.mcphoton.world.Location;

/**
 * Handles the login of a Player.
 *
 * @author TheElectronWill
 */
public class LoginHandlerImpl implements ServerLoginHandler {
	@Override
	public void loggedIn(Session session) {
		// Send player infos
		session.send(
				new ServerJoinGamePacket(0, false, GameMode.SURVIVAL, 0, Difficulty.PEACEFUL, 10,
										 WorldType.DEFAULT, false));
		session.send(new ServerSpawnPositionPacket(new IntPosition(0, 0, 0)));
		session.send(new ServerPlayerAbilitiesPacket(false, true, false, true, 1f, 1f));
		Location spawnLocation = PhotonServer.Config().spawnLocation();
		session.send(new ServerPlayerPositionRotationPacket(spawnLocation.toVec3d(), 0f, 0f, 0));

		// Send chunks
		int cx = spawnLocation.blockX() / 16;
		int cz = spawnLocation.blockZ() / 16;
		for (int dx = -2; dx <= 2; dx++) {
			for (int dz = -2; dz <= 2; dz++) {
				ChunkColumnImpl chunkColumn = spawnLocation.world()
														   .chunkGenerator()
														   .generate(cx + dx, cz + dz);
				session.send(new ServerChunkDataPacket(chunkColumn.getData()));
			}
		}

		// Send welcome message
		session.send(new ServerChatPacket(
				"Welcome to the Photon server! Version " + PhotonServer.Version()));
	}
}