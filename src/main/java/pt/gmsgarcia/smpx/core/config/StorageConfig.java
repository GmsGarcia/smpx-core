package pt.gmsgarcia.smpx.core.config;

import org.bukkit.configuration.file.YamlConfiguration;

public class StorageConfig {
    private final String type;
    private final DatabaseConfig database = new DatabaseConfig();

    public StorageConfig(YamlConfiguration config) {
        type = config.getString("storage.type");

        database.host = config.getString("storage.database.host");
        database.port = config.getString("storage.database.port");
        database.user = config.getString("storage.database.user");
        database.password = config.getString("storage.database.password");
        database.database = config.getString("storage.database.database");
    }

    public String type() {
        return this.type;
    }

    public DatabaseConfig database() {
        return this.database;
    }

    public static class DatabaseConfig {
        public String host;
        public String port;
        public String user;
        public String password;
        public String database;
    }
}