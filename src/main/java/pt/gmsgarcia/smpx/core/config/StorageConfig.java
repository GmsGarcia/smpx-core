package pt.gmsgarcia.smpx.core.config;

import org.bukkit.configuration.file.YamlConfiguration;

public class StorageConfig {
    private final String type;

    private final YamlConfig yaml = new YamlConfig();
    private final MySQLConfig mysql = new MySQLConfig();

    public StorageConfig(YamlConfiguration config) {
        type = config.getString("storage.type");

        yaml.path = config.getString("storage.yaml.path");
        yaml.filename = config.getString("storage.yaml.filename");

        mysql.host = config.getString("storage.mysql.host");
        mysql.port = config.getString("storage.mysql.port");
        mysql.user = config.getString("storage.mysql.user");
        mysql.password = config.getString("storage.mysql.password");
        mysql.database = config.getString("storage.mysql.database");
    }

    public String type() {
        return this.type;
    }

    public YamlConfig yaml() {
        return this.yaml;
    }

    public MySQLConfig mysql() {
        return this.mysql;
    }

    public static class YamlConfig {
        public String path;
        public String filename;
    }

    public static class MySQLConfig {
        public String host;
        public String port;
        public String user;
        public String password;
        public String database;
    }
}