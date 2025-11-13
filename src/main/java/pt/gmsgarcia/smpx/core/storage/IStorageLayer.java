package pt.gmsgarcia.smpx.core.storage;

import pt.gmsgarcia.smpx.core.account.Account;
import pt.gmsgarcia.smpx.core.user.User;

import java.util.HashMap;
import java.util.UUID;

public interface IStorageLayer {
    void init();
    User getUser(UUID uuid);
    HashMap<String, Account> getAccounts(UUID uuid);
    void saveUser(User user);
    void saveAccounts(UUID uuid, HashMap<String, Account> accounts);
    void createUser(User user);
    void createAccounts(UUID uuid, HashMap<String, Account> accounts);
    void createPreviousUsername(User user);
}