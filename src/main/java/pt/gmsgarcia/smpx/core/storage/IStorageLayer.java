package pt.gmsgarcia.smpx.core.storage;

import pt.gmsgarcia.smpx.core.user.User;

import java.util.UUID;

public interface IStorageLayer {
    void init();
    User load(UUID uuid);
    void save(User user);
    void savePreviousName(User user);
    void create(User user);
}
