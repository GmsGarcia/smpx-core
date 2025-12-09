package pt.gmsgarcia.smpx.core.storage.layers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.account.Account;
import pt.gmsgarcia.smpx.core.storage.IStorageLayer;
import pt.gmsgarcia.smpx.core.user.User;
import pt.gmsgarcia.smpx.core.user.Username;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

        if (!file.exists()) {
            SmpxCore.logger().warning("User file with UUID " + uuid + " does not exist.");
            return null;
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        String username = yaml.getString("name");
        long joinDate = yaml.getLong("join-date");
        long lastSeen = yaml.getLong("last-seen");

        // previous usernames...
        List<?> raw = yaml.getList("previous-names", new ArrayList<>());
        ArrayList<Username> previousUsernames = new ArrayList<>();

        for (Object obj : raw) {
            if (obj instanceof Username user) {
                previousUsernames.add(user);

            } else if (obj instanceof Map<?, ?> map) {
                previousUsernames.add(
                        Username.deserialize((Map<String, Object>) map)
                );
            }
        }

        return User.build(uuid, username, joinDate, lastSeen, previousUsernames);
    }

    @Override
    public HashMap<String, Account> getAccounts(UUID uuid) {
        File file = new File(wallets, uuid + ".yml");

        if (!file.exists()) {
            SmpxCore.logger().warning("User file with UUID " + uuid + " does not exist.");
            return null;
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        HashMap<String, Account> accounts = new HashMap<>();

        ConfigurationSection section = yaml.getConfigurationSection("accounts");
        if (section == null) return accounts;

        for (String key : section.getKeys(false)) {
            Object raw = section.get(key);

            if (raw instanceof Account acc) {
                accounts.put(key, acc);

            } else if (raw instanceof Map<?, ?> map) {
                Account acc = Account.deserialize((Map<String, Object>) map);
                accounts.put(key, acc);
            }
        }

        return accounts;
    }

    @Override
    public void saveUser(User user) {
        File file = new File(users, user.uuid() + ".yml");
        YamlConfiguration yaml = new YamlConfiguration();

        yaml.set("name", user.name());
        yaml.set("join-date", user.joinDate());
        yaml.set("last-seen", user.lastSeen());

        try {
            yaml.save(file);
        } catch (IOException e) {
            SmpxCore.logger().severe("YamlFileStorage.save error: " + e.getMessage());
        }
    }

    @Override
    public void saveAccounts(UUID uuid, HashMap<String, Account> accounts) {
        File file = new File(wallets, uuid + ".yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        yaml.set("accounts", accounts);

        try {
            yaml.save(file);
            SmpxCore.logger().info("Accounts for user " + uuid + " saved successfully.");
        } catch (IOException e) {
            SmpxCore.logger().severe("Failed to save accounts for user " + uuid + ": " + e.getMessage());
        }
    }

    @Override
    public void createUser(User user) {
        File file = new File(users, user.uuid() + ".yml");
        YamlConfiguration yaml = new YamlConfiguration();

        yaml.set("name", user.name());
        yaml.set("join-date", user.joinDate());
        yaml.set("last-seen", user.lastSeen());

        try {
            yaml.save(file);
        } catch (IOException e) {
            SmpxCore.logger().severe("YamlFileStorage.save error: " + e.getMessage());
        }
    }

    @Override
    public void createAccounts(UUID uuid, HashMap<String, Account> accounts) {
        File file = new File(wallets, uuid + ".yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        yaml.set("accounts", accounts);

        try {
            yaml.save(file);
            SmpxCore.logger().info("Accounts for user " + uuid + " saved successfully.");
        } catch (IOException e) {
            SmpxCore.logger().severe("Failed to save accounts for user " + uuid + ": " + e.getMessage());
        }
    }

    @Override
    public void createPreviousUsername(User user) {

    }
}