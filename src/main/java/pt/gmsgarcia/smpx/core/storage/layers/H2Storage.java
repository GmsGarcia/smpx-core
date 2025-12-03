package pt.gmsgarcia.smpx.core.storage.layers;

import pt.gmsgarcia.smpx.core.account.Account;
import pt.gmsgarcia.smpx.core.storage.IStorageLayer;
import pt.gmsgarcia.smpx.core.user.User;

import java.util.HashMap;
import java.util.UUID;

public class H2Storage implements IStorageLayer {
    @Override
    public void init() {}

    @Override
    public User getUser(UUID uuid) {
        return null;
    }

    @Override
    public HashMap<String, Account> getAccounts(UUID uuid) {
        return null;
    }

    @Override
    public void saveUser(User user) {

    }

    @Override
    public void saveAccounts(UUID uuid, HashMap<String, Account> accounts) {

    }

    @Override
    public void createUser(User user) {

    }

    @Override
    public void createAccounts(UUID uuid, HashMap<String, Account> accounts) {

    }

    @Override
    public void createPreviousUsername(User user) {

    }
    // TODO...
}