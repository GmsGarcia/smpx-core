package pt.gmsgarcia.smpx.core.config;

import org.bukkit.configuration.file.YamlConfiguration;
import pt.gmsgarcia.smpx.core.SmpxCore;

import java.io.File;

public class SmpxConfig {
    private File file;
    private YamlConfiguration config;

    private EconomyConfig economy;
    private StorageConfig storage;

    public SmpxConfig() {
    }

    public void load() {
        file = new File(SmpxCore.instance().getDataFolder(), "config.yml");

        if (!file.exists()) {
            SmpxCore.instance().saveResource("config.yml", false);
        }

        config = new YamlConfiguration();
        config.options().parseComments(true);

        try {
            config = YamlConfiguration.loadConfiguration(file);
            SmpxCore.logger().info("Config loaded.");
        } catch (Exception e) {
            SmpxCore.logger().severe("Failed to load config: " + e.getMessage());
        }

        // economy config
        this.economy = new EconomyConfig(config);

        // storage config
        this.storage = new StorageConfig(config);
    }

    public void replace() {
        SmpxCore.instance().saveResource("config.yml", true);
        this.load();
    }

    public void save() {
        try {
            config.save(file);
            SmpxCore.logger().info("Config saved");
        } catch (Exception e) {
            SmpxCore.logger().severe("Failed to save config: " + e.getMessage());
        }
    }

    public EconomyConfig economy() {
        return this.economy;
    }

    public StorageConfig storage() {
        return this.storage;
    }
}