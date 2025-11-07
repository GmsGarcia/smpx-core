package pt.gmsgarcia.smpx.core.storage.layers;

import pt.gmsgarcia.smpx.core.storage.IStorageLayer;
import pt.gmsgarcia.smpx.core.user.User;

import java.util.UUID;

public class YamlFileStorage implements IStorageLayer {
    @Override
    public void init() {

    }

    @Override
    public User load(UUID uuid) {
        return null;
    }

    @Override
    public void save(User user) {

    }

    @Override
    public void savePreviousName(User user) {

    }

    @Override
    public void create(User user) {

    }
}