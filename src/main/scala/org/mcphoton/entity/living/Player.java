package org.mcphoton.entity.living;

import org.mcphoton.messaging.Messageable;
import org.mcphoton.user.User;

public interface Player extends User, Messageable {
    @Override
    default boolean isOnline() {
        return true;
    }
}