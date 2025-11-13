package pt.gmsgarcia.smpx.core.storage.layers.sql.dao;

import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.storage.layers.sql.DatabaseManager;
import pt.gmsgarcia.smpx.core.user.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserDAO {
    private final DatabaseManager db;

    public UserDAO(DatabaseManager db) {
        this.db = db;
    }

    public User load(UUID uuid) {
        String sql = "SELECT uuid, name, join_date, last_seen FROM users WHERE uuid = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                String name = rs.getString("name");
                long joinDate = rs.getLong("join_date");
                long lastSeen = rs.getLong("last_seen");

                return User.build(uuid, name, joinDate, lastSeen, null);
            }
        } catch (SQLException ex) {
            SmpxCore.logger().severe("UserDao.load error: " + ex.getMessage());
            return null;
        }
    }

    public void create(User user) {
        String sql = "INSERT INTO users (uuid, name, join_date, last_seen) VALUES (?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.uuid().toString());
            ps.setString(2, user.name());
            ps.setLong(3, user.joinDate());
            ps.setLong(4, user.lastSeen());
            ps.executeUpdate();
        } catch (SQLException ex) {
            SmpxCore.logger().severe("UserDao.create error: " + ex.getMessage());
        }
    }

    public void save(User user) {
        String sql = "UPDATE users SET name = ?, last_seen = ? WHERE uuid = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.name());
            ps.setLong(2, user.lastSeen());
            ps.setString(3, user.uuid().toString());
            ps.executeUpdate();
        } catch (SQLException ex) {
            SmpxCore.logger().severe("UserDao.save error: " + ex.getMessage());
        }
    }
}