package pt.gmsgarcia.smpx.core.storage.layers;

import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.account.Account;
import pt.gmsgarcia.smpx.core.config.StorageConfig.MySQLConfig;
import pt.gmsgarcia.smpx.core.storage.IStorageLayer;
import pt.gmsgarcia.smpx.core.storage.layers.sql.DatabaseManager;
import pt.gmsgarcia.smpx.core.storage.layers.sql.dao.AccountDAO;
import pt.gmsgarcia.smpx.core.storage.layers.sql.dao.UserDAO;
import pt.gmsgarcia.smpx.core.storage.layers.sql.dao.UsernameDAO;
import pt.gmsgarcia.smpx.core.user.User;

import java.util.HashMap;
import java.util.UUID;

public class MySQLStorage implements IStorageLayer {
    private DatabaseManager db;
    private UserDAO userDao;
    private AccountDAO accountDao;
    private UsernameDAO usernameDao;

    @Override
    public void init() {
        MySQLConfig cfg = SmpxCore.config().storage().mysql();
        this.db = new DatabaseManager(cfg);
        this.userDao = new UserDAO(db);
        this.accountDao = new AccountDAO(db);
        this.usernameDao = new UsernameDAO(db);
    }

    @Override
    public User getUser(UUID uuid) {
        return userDao.load(uuid);
    }

    @Override
    public HashMap<String, Account> getAccounts(UUID uuid) {
        return accountDao.load(uuid);
    }

    @Override
    public void saveUser(User user) {
        if (user == null) return;
        userDao.save(user);
    }

    @Override
    public void saveAccounts(UUID uuid, HashMap<String, Account> accounts) {
        if (accounts == null || accounts.isEmpty()) return;
        accountDao.save(uuid, accounts);
    }

    @Override
    public void createUser(User user) {
        userDao.create(user);
    }

    @Override
    public void createAccounts(UUID uuid, HashMap<String, Account> accounts) {
        accountDao.create(uuid, accounts);
    }

    @Override
    public void createPreviousUsername(User user) {
        usernameDao.create(user);
    }
}