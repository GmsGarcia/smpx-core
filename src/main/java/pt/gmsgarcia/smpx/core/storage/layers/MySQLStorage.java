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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
        } catch (Exception e) {
            SmpxCore.logger().warning("Failed to connect to MySQL database.");
        }

        verifyTableIntegrity();
    }

    private void verifyTableIntegrity() {
        String tableName = "users";
        Set<String> requiredColumns = Set.of("uuid", "name", "balance");

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData meta = connection.getMetaData();

            // Check if table exists
            try (ResultSet tables = meta.getTables(null, null, tableName, null)) {
                if (!tables.next()) {
                    SmpxCore.logger().severe("Table 'users' not found. Creating it (Not implemented)...");
                    //createUsersTable();
                    return;
                }
            }

            // Check columns
            Set<String> existingColumns = new HashSet<>();
            try (ResultSet columns = meta.getColumns(null, null, tableName, null)) {
                while (columns.next()) {
                    existingColumns.add(columns.getString("COLUMN_NAME").toLowerCase());
                }
            }

            for (String col : requiredColumns) {
                if (!existingColumns.contains(col.toLowerCase())) {
                    SmpxCore.logger().severe("Column '" + col + "' missing. Adding it (Not implemented)...");
                    //addMissingColumn(connection, tableName, col);
                }
            }

            SmpxCore.logger().info("Database table '" + tableName + "' integrity verified.");
        } catch (SQLException e) {
            SmpxCore.logger().severe("Failed to verify database integrity: " + e.getMessage());
        }
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

                SmpxCore.logger().warning("No user found with UUID " + uuid + " in database.");
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
                    SmpxCore.logger().warning("Users with uuid " + uuid + " has no previous usernames in database.");
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