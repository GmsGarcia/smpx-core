package pt.gmsgarcia.smpx.core.storage.layers.sql.dao;

import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.storage.layers.sql.DatabaseManager;
import pt.gmsgarcia.smpx.core.user.User;
import pt.gmsgarcia.smpx.core.user.Username;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UsernameDAO {
    private final DatabaseManager db;

    public UsernameDAO(DatabaseManager db) {
        this.db = db;
    }

    public List<Username> load(UUID uuid) {
        String sql = "SELECT name, last_usage FROM previous_usernames WHERE uuid = ? ORDER BY last_usage DESC";
        List<Username> list = new ArrayList<>();

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Username(rs.getString("name"), rs.getLong("last_usage")));
                }
            }
        } catch (SQLException ex) {
            SmpxCore.logger().severe("UsernameDao.loadPreviousNames error: " + ex.getMessage());
        }

        return list;
    }

    public void create(User user) {
        String sql = "INSERT INTO previous_usernames (uuid, name, last_usage) VALUES (?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.uuid().toString());
            ps.setString(2, user.name());
            ps.setLong(3, user.lastSeen());
            ps.executeUpdate();
        } catch (SQLException ex) {
            SmpxCore.logger().severe("UsernameDao.addPreviousName error: " + ex.getMessage());
        }
    }
}