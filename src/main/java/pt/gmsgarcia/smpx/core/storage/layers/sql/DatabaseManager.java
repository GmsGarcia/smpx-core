package pt.gmsgarcia.smpx.core.storage.layers.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.config.StorageConfig;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    private final HikariDataSource dataSource;

    public DatabaseManager(StorageConfig.MySQLConfig cfg) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://" + cfg.host + ":" + cfg.port + "/" + cfg.database);
        config.setUsername(cfg.user);
        config.setPassword(cfg.password);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(1);
        config.setPoolName("smpx-pool");

        this.dataSource = new HikariDataSource(config);

        try (var conn = this.getConnection()) {
            verifyDatabaseIntegrity(conn);
            SmpxCore.logger().info("DatabaseManager initialized");
        } catch (Exception ex) {
            SmpxCore.logger().severe("DatabaseManager failed to load: " + ex.getMessage());
        }
    }

    public final Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private void verifyDatabaseIntegrity(Connection conn) {
        String users = "CREATE TABLE IF NOT EXISTS users (\n" +
                "  uuid CHAR(36) PRIMARY KEY,\n" +
                "  name VARCHAR(16) NOT NULL,\n" +
                "  join_date BIGINT NOT NULL,\n" +
                "  last_seen BIGINT DEFAULT NULL\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        String wallets = "CREATE TABLE IF NOT EXISTS wallets (\n" +
                "  uuid CHAR(36) NOT NULL,\n" +
                "  name VARCHAR(64) NOT NULL,\n" +
                "  currency VARCHAR(10) NOT NULL,\n" +
                "  balance DECIMAL(30,8) NOT NULL DEFAULT 0,\n" +
                "  INDEX (uuid),\n" +
                "  PRIMARY KEY (uuid, currency)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        String prev = "CREATE TABLE IF NOT EXISTS previous_usernames (\n" +
                "  uuid CHAR(36) NOT NULL,\n" +
                "  name VARCHAR(64) NOT NULL,\n" +
                "  last_usage BIGINT NOT NULL,\n" +
                "  INDEX (uuid),\n" +
                "  CONSTRAINT fk_prev_user_uuid FOREIGN KEY (uuid) REFERENCES users(uuid) ON DELETE CASCADE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";

        try (var stmt = conn.createStatement()) {
            stmt.execute(users);
            stmt.execute(wallets);
            stmt.execute(prev);
        } catch (Exception ex) {
            SmpxCore.logger().severe("verifyDatabaseIntegrity failed: " + ex.getMessage());
        }
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) dataSource.close();
    }
}