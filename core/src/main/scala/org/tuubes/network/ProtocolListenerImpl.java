package org.tuubes.network;

import com.github.steveice10.mc.auth.data.GameProfile;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.data.message.ChatColor;
import com.github.steveice10.mc.protocol.data.message.ChatFormat;
import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.data.message.MessageStyle;
import com.github.steveice10.mc.protocol.data.message.TextMessage;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.packetlib.event.server.ServerAdapter;
import com.github.steveice10.packetlib.event.server.SessionAddedEvent;
import com.github.steveice10.packetlib.event.server.SessionRemovedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

/**
 * Handles low-level protocol events, like a new connection.
 *
 * @author TheElectronWill
 */
public class ProtocolListenerImpl extends ServerAdapter {
	@Override
	public void sessionAdded(SessionAddedEvent event) {
		event.getSession().addListener(new SessionAdapter() {
			@Override
			public void packetReceived(PacketReceivedEvent event) {
				if (event.getPacket() instanceof ClientChatPacket) {
					ClientChatPacket packet = event.getPacket();
					GameProfile profile = event.getSession()
											   .getFlag(MinecraftConstants.PROFILE_KEY);
					System.out.println(profile.getName() + ": " + packet.getMessage());
					Message msg = new TextMessage("Hello, ").setStyle(
							new MessageStyle().setColor(ChatColor.GREEN));
					Message name = new TextMessage(profile.getName()).setStyle(
							new MessageStyle().setColor(ChatColor.AQUA)
											  .addFormat(ChatFormat.UNDERLINED));
					Message end = new TextMessage("!");
					msg.addExtra(name);
					msg.addExtra(end);
					event.getSession().send(new ServerChatPacket(msg));
				}
			}
		});
	}

	@Override
	public void sessionRemoved(SessionRemovedEvent event) {
		MinecraftProtocol protocol = (MinecraftProtocol)event.getSession().getPacketProtocol();
		if (protocol.getSubProtocol() == SubProtocol.GAME) {
			System.out.println("Closing server.");
			event.getServer().close();
		}
	}
}
