package org.mcphoton.entity.living;

import org.mcphoton.messaging.Messageable;
import org.mcphoton.user.User;
import org.mcphoton.utils.Location;

public interface Player extends User, LivingEntity, Messageable {

	@Override
	default boolean isOnline() {
		return true;
	}

	String getNameInChat();

	void setNameInChat(String name);

	String getNameInPlayerList();

	void setNameInPlayerList(String name);

	Location getCompassTarget();

	void setCompassTarget(Location target);

	void kickPlayer(String message);

	void sendRawMessage(String rawMessage);
}