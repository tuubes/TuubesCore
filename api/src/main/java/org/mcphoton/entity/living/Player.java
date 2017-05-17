package org.mcphoton.entity.living;

import org.mcphoton.messaging.Messageable;
import org.mcphoton.user.User;
import org.mcphoton.utils.Location;

public interface Player extends User, Messageable {
    @Override
    default boolean isOnline() {
        return true;
    }
}