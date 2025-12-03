package pt.gmsgarcia.smpx.core.storage.layers;

import org.bukkit.configuration.file.YamlConfiguration;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.account.Account;
import pt.gmsgarcia.smpx.core.storage.IStorageLayer;
import pt.gmsgarcia.smpx.core.user.User;
import pt.gmsgarcia.smpx.core.user.Username;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class YamlFileStorage implements IStorageLayer {
    File users = new File(SmpxCore.instance().getDataFolder(), "users");
    File wallets = new File(SmpxCore.instance().getDataFolder(), "wallets");

    @Override
    public void init() {
        if (!users.exists()) {
            if (!users.mkdirs()) {
                SmpxCore.logger().severe("Unable to create users directory at: " + users.getAbsolutePath());
            }
        }

        if (!wallets.exists()) {
            if (!wallets.mkdirs()) {
                SmpxCore.logger().severe("Unable to create wallets directory at: " + wallets.getAbsolutePath());
            }
        }
    }

    @Override
    public User getUser(UUID uuid) {
        File file = new File(users, uuid + ".yml");
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.options().parseComments(true);

        try {
            yaml = YamlConfiguration.loadConfiguration(file);
            SmpxCore.logger().info("User file with UUID " + uuid + " loaded.");
        } catch (Exception e) {
            SmpxCore.logger().severe("Failed to load user file with UUID: " + e.getMessage());
            return null;
        }

        String username = yaml.getString("username");
        long joinDate = yaml.getLong("join-date");
        long lastSeen = yaml.getLong("last-seen");

        List<?> raw = yaml.getList("previous-usernames", new ArrayList<>());
        ArrayList<Username> previousUsernames = new ArrayList<>(raw.size());

        for (Object obj : raw) {
            if (obj instanceof Username user) {
                previousUsernames.add(user);
            }
        }

        return User.build(uuid, username, joinDate, lastSeen, previousUsernames);
    }

    @Override
    public HashMap<String, Account> getAccounts(UUID uuid) {
        File file = new File(users, uuid + ".yml");
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.options().parseComments(true);

        try {
            yaml = YamlConfiguration.loadConfiguration(file);
            SmpxCore.logger().info("Account file with UUID " + uuid + " loaded.");
        } catch (Exception e) {
            SmpxCore.logger().severe("Failed to load account file with UUID: " + e.getMessage());
            return null;
        }

        List<?> raw = yaml.getList("accounts", new ArrayList<>());
        HashMap<String, Account> accounts = new HashMap<>();

        for (Object obj : raw) {
            if (obj instanceof Account acc) {
                accounts.put(acc.currency(), acc);
            }
        }

        return accounts;
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
}