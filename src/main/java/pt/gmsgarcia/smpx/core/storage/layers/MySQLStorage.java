package pt.gmsgarcia.smpx.core.storage.layers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.config.StorageConfig;
import pt.gmsgarcia.smpx.core.storage.IStorageLayer;
import pt.gmsgarcia.smpx.core.user.User;
import pt.gmsgarcia.smpx.core.user.UserName;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class MySQLStorage implements IStorageLayer {

    private HikariDataSource dataSource;

    @Override
    public void init() {
        StorageConfig.MySQLConfig mysql = SmpxCore.config().storage().mysql();
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://" + mysql.host + ":" + mysql.port + "/" + mysql.database);
        config.setUsername(mysql.user);
        config.setPassword(mysql.password);
        config.setMaximumPoolSize(10);
        config.addDataSourceProperty("", "");

        this.dataSource = new HikariDataSource(config);

        try (Connection conn = dataSource.getConnection()) {
            SmpxCore.logger().info("Successfully connected to MySQL database.");
            verifyDatabaseIntegrity(conn);
        } catch (Exception e) {
            SmpxCore.logger().severe("Failed to connect to MySQL database.");
        }
    }

    private void verifyDatabaseIntegrity(Connection conn) {
        LinkedHashMap<String, Set<String>> tables = new LinkedHashMap<>();
        tables.put("users", Set.of("uuid", "name", "balance", "join_date", "last_seen"));
        tables.put("previous_usernames", Set.of("uuid", "name", "last_usage"));

        try {
            DatabaseMetaData meta = conn.getMetaData();

            for (Map.Entry<String, Set<String>> table : tables.entrySet()) {
                try (ResultSet rs = meta.getTables(conn.getCatalog(), null, table.getKey(), null)) {
                    if (!rs.next()) {
                        SmpxCore.logger().warning("Table '" + table.getKey() + "' not found. Creating it...");
                        createTable(conn, table.getKey());
                        continue;
                    }
                }

                Set<String> columns = new HashSet<>();
                try (ResultSet rs = meta.getColumns(conn.getCatalog(), null, table.getKey(), null)) {
                    while (rs.next()) {
                        columns.add(rs.getString("COLUMN_NAME").toLowerCase());
                    }
                }

                for (String column : table.getValue()) {
                    if (!columns.contains(column.toLowerCase())) {
                        SmpxCore.logger().warning("Column '" + column + "' missing. Adding it...");
                        addMissingColumn(conn, table.getKey(), column);
                    }
                }

                SmpxCore.logger().info("Database table '" + table.getKey() + "' integrity verified.");
            }
        } catch (SQLException e) {
            SmpxCore.logger().severe("Failed to verify database integrity: " + e.getMessage());
        }
    }

    private void createTable(Connection conn, String table) {
        String sql;

        switch (table.toLowerCase()) {
            case "users" -> sql = """
                CREATE TABLE IF NOT EXISTS users (
                    uuid CHAR(36) PRIMARY KEY UNIQUE,
                    name VARCHAR(16) NOT NULL,
                    balance DECIMAL(18,2) NOT NULL DEFAULT 0,
                    join_date BIGINT NOT NULL,
                    last_seen BIGINT DEFAULT NULL
                )
            """;
            case "previous_usernames" -> sql = """
                CREATE TABLE IF NOT EXISTS previous_usernames (
                    uuid CHAR(36) NOT NULL,
                    name VARCHAR(16) NOT NULL,
                    last_usage BIGINT NOT NULL,
                    INDEX(uuid),
                    CONSTRAINT fk_prev_user_uuid FOREIGN KEY (uuid) REFERENCES users(uuid) ON DELETE CASCADE
                )
            """;
            default -> {
                SmpxCore.logger().warning("No create definition for table: " + table);
                return;
            }
        }

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            SmpxCore.logger().info("Created table '" + table + "'.");
        } catch (SQLException e) {
            SmpxCore.logger().severe("Failed to create table '" + table + "': " + e.getMessage());
        }
    }

    private void addMissingColumn(Connection conn, String table, String column) {
        String sql = "ALTER TABLE `" + table + "` ADD COLUMN `" + column + "` " + getDefinition(column.toLowerCase());

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            SmpxCore.logger().info("Added missing column '" + column + "' to table '" + table + "'.");
        } catch (SQLException e) {
            SmpxCore.logger().severe("Failed to add column '" + column + "' to table '" + table + "': " + e.getMessage());
        }
    }

    private String getDefinition(String column) {
        return switch (column.toLowerCase()) {
            case "uuid" -> "CHAR(36) NOT NULL";
            case "name" -> "VARCHAR(64) NOT NULL";
            case "balance" -> "DECIMAL(18,2) NOT NULL DEFAULT 0";
            case "join_date", "last_seen", "last_usage" -> "BIGINT NOT NULL DEFAULT 0";
            default -> "VARCHAR(255)";
        };
    }

    @Override
    public User load(UUID uuid) {
        String query = "SELECT * FROM users WHERE uuid = ?";
        try (Connection conn = this.dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, uuid.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    BigDecimal balance = rs.getBigDecimal("balance");
                    long joinDate  = rs.getLong("join_date");
                    long lastSeen  = rs.getLong("last_seen");
                    ArrayList<UserName> previousNames = this.loadPreviousNames(uuid, conn);

                    SmpxCore.logger().info("User with UUID " + uuid + " found. name: " + name + " balance: " + balance + " joinDate: " + joinDate);
                    return new User(uuid, name, balance, joinDate, lastSeen, previousNames);
                }

                SmpxCore.logger().info("No user found with UUID " + uuid + " in database.");
                return null;
            }
        } catch (SQLException e) {
            SmpxCore.logger().severe("Failed to load user with UUID " + uuid.toString() + ": " + e.getMessage());
            return null;
        }
    }

    private ArrayList<UserName> loadPreviousNames(UUID uuid, Connection conn) {
        String query = "SELECT * FROM previous_usernames WHERE uuid = ? ORDER BY last_usage DESC";
        if (conn == null) {
            try {
                conn = this.dataSource.getConnection();
            } catch (SQLException e) {
                SmpxCore.logger().severe("Failed to load previous usernames for user with UUID " + uuid.toString() + ": " + e.getMessage());
                return null;
            }
        }

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, uuid.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                ArrayList<UserName> names = new ArrayList<>();
                while (rs.next()) {
                    String name = rs.getString("name");
                    long lastUsage  = rs.getLong("last_usage");
                    names.add(new UserName(name, lastUsage));
                }

                if (names.isEmpty()) {
                    SmpxCore.logger().info("Users with uuid " + uuid + " has no previous usernames in database.");
                    return null;
                }

                return names;
            }
        } catch (SQLException e) {
            SmpxCore.logger().severe("Failed to load previous usernames for user with UUID " + uuid.toString() + ": " + e.getMessage());
            return null;
        }
    }

    @Override
    public void save(User user) {
        String query = "UPDATE users SET name = ?, balance = ?, last_seen = ? WHERE uuid = ?";
        try (PreparedStatement stmt = this.dataSource.getConnection().prepareStatement(query)) {
            stmt.setString(1, user.name());
            stmt.setBigDecimal(2, user.balance());
            stmt.setLong(3, user.lastSeen());
            stmt.setString(4, user.uuid().toString());
            stmt.executeUpdate();

            SmpxCore.logger().info("Successfully updated user with UUID " + user.uuid().toString());
        } catch (SQLException e) {
            // this is critical for now, since it only saves userdata when logging out!
            // i need to implement some periodic saves...
            SmpxCore.logger().severe("Failed to update user with UUID " + user.uuid().toString() + ": " + e.getMessage());
        }
    }

    @Override
    public void savePreviousName(User user) {
        String query = "INSERT INTO previous_usernames (uuid, name, last_usage) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = this.dataSource.getConnection().prepareStatement(query)) {
            stmt.setString(1, user.uuid().toString());
            stmt.setString(2, user.name());
            stmt.setLong(3, user.lastSeen());
            stmt.executeUpdate();
            SmpxCore.logger().info("Successfully updated previous usernames for user with UUID " + user.uuid().toString());
        } catch (SQLException e) {
            SmpxCore.logger().severe("Failed to update previous usernames for user with UUID " + user.uuid().toString() + ": " + e.getMessage());
        }
    }

    @Override
    public void create(User user) {
        String query = "INSERT INTO users (uuid, name, balance, join_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = this.dataSource.getConnection().prepareStatement(query)) {
            stmt.setString(1, user.uuid().toString());
            stmt.setString(2, user.name());
            stmt.setBigDecimal(3, user.balance());
            stmt.setLong(4, user.joinDate());
            stmt.executeUpdate();
            SmpxCore.logger().info("Created entry for user with UUID " + user.uuid() + " in database.");
        } catch (SQLException e) {
            // something went wrong...
            SmpxCore.logger().severe("Failed to create user with UUID " + user.uuid().toString() + ": " + e.getMessage());
        }
    }
}