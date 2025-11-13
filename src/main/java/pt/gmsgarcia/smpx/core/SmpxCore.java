package pt.gmsgarcia.smpx.core;

import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.gmsgarcia.smpx.core.account.AccountsCache;
import pt.gmsgarcia.smpx.core.commands.CommandRegister;
import pt.gmsgarcia.smpx.core.config.SmpxConfig;
import pt.gmsgarcia.smpx.core.economy.EconomyBridge;
import pt.gmsgarcia.smpx.core.economy.vault.VaultEconomyProvider;
import pt.gmsgarcia.smpx.core.economy.vault.VaultUnlockedEconomyProvider;
import pt.gmsgarcia.smpx.core.listeners.SmpxListeners;
import pt.gmsgarcia.smpx.core.logger.SmpxLogger;
import pt.gmsgarcia.smpx.core.messages.MessageManager;
import pt.gmsgarcia.smpx.core.storage.StorageManager;
import pt.gmsgarcia.smpx.core.user.UsersCache;

public final class SmpxCore extends JavaPlugin {
    private static SmpxLogger logger;
    private static SmpxConfig config;
    private static MessageManager messages;
    private static StorageManager storage;
    private static EconomyBridge economy;
    private static UsersCache users;
    private static AccountsCache accounts;

    private static VaultEconomyProvider vaultEconomyProvider;
    private static VaultUnlockedEconomyProvider vaultUnlockedEconomyProvider;

    @Override
    public void onEnable() {
        logger = new SmpxLogger(this.getLogger());

        config = new SmpxConfig();
        config.load();

        messages = new MessageManager();
        messages.load();

        storage = new StorageManager();
        storage.init();

        users = new UsersCache();
        accounts = new AccountsCache();

        economy = new EconomyBridge();

        // vault api
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            // vault unlocked
            try {
                Class.forName("net.milkbowl.vault2.economy.Economy");
                vaultUnlockedEconomyProvider = new VaultUnlockedEconomyProvider();
                getServer().getServicesManager().register(net.milkbowl.vault2.economy.Economy.class, vaultUnlockedEconomyProvider, this, ServicePriority.Highest);
                logger().info("Hooked into Vault Unlocked as an Economy provider!");
            } catch (Exception e) {
                logger().warning("Unable to connect to Vault Unlocked");
            }

            // vault
            try {
                Class.forName("net.milkbowl.vault.economy.Economy");
                vaultEconomyProvider = new VaultEconomyProvider();
                getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, vaultEconomyProvider, this, ServicePriority.Highest);
                logger().info("Hooked into Vault as an Economy provider!");
            } catch (Exception e) {
                logger().warning("Unable to connect to Vault");
            }
        }

        // commands
        CommandRegister.registerCommands();

        // listeners
        SmpxListeners.registerListeners();

        logger.info("SmpxCore enabled");
    }

    @Override
    public void onDisable() {
        users().save(null);
    }

    // getters...
    public static SmpxCore instance() {
        return getPlugin(SmpxCore.class);
    }

    public static SmpxLogger logger() {
        return logger;
    }

    public static SmpxConfig config() {
        return config;
    }

    public static MessageManager messages() {
        return messages;
    }

    public static StorageManager storage() {
        return storage;
    }

    public static UsersCache users() {
        return users;
    }

    public static AccountsCache accounts() {
        return accounts;
    }

    public static EconomyBridge economy() {
        return economy;
    }
}