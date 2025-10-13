package pt.gmsgarcia.smpx.core;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import pt.gmsgarcia.smpx.core.commands.CommandRegister;
import pt.gmsgarcia.smpx.core.config.SmpxConfig;
import pt.gmsgarcia.smpx.core.listeners.SmpxListeners;
import pt.gmsgarcia.smpx.core.logger.SmpxLogger;
import pt.gmsgarcia.smpx.core.messages.MessageManager;
import pt.gmsgarcia.smpx.core.storage.StorageManager;
import pt.gmsgarcia.smpx.core.user.UserMap;

public final class SmpxCore extends JavaPlugin {
    private static SmpxLogger logger;
    private static SmpxConfig config;
    private static MessageManager messages;
    private static StorageManager storage;
    private static UserMap userCache;

    @Override
    public void onEnable() {
        logger = new SmpxLogger(this.getLogger());

        config = new SmpxConfig();
        config.load();

        messages = new MessageManager();
        messages.load();

        storage = new StorageManager();
        storage.init();

        userCache = new UserMap();

        // commands
        CommandRegister.registerCommands();

        // listeners
        SmpxListeners.registerListeners();

        logger.info("SmpxCore enabled");
    }

    @Override
    public void onDisable() {
        users().saveAll();
    }

    public static void reload() {
        messages.load();
        logger.info("SmpxCore reloaded");
    }

    public static void replace() {
        messages.replace();
        logger.info("SmpxCore files replaced");
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

    public static UserMap users() {
        return userCache;
    }

    public static void runTaskAsync(Runnable task) {
        try {
            Bukkit.getScheduler().runTaskAsynchronously(instance(), task);
        } catch (IllegalArgumentException e) {
            SmpxCore.logger().severe(e.getMessage());
        }
    }

    public static void runTask(Runnable task) {
        try {
            Bukkit.getScheduler().runTaskAsynchronously(instance(), task);
        } catch (IllegalArgumentException e) {
            SmpxCore.logger().severe(e.getMessage());
        }
    }
}