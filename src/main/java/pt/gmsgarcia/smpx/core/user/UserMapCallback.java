package pt.gmsgarcia.smpx.core.user;

import java.util.HashMap;
import java.util.UUID;

public interface UserMapCallback {
    default void onUserGet(User user) {}
    default void onUsersGet(HashMap<UUID, User> users) {}
}
