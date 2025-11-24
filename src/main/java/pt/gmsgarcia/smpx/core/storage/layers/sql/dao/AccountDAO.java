package pt.gmsgarcia.smpx.core.storage.layers.sql.dao;

import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.account.Account;
import pt.gmsgarcia.smpx.core.storage.layers.sql.DatabaseManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AccountDAO {
    private final DatabaseManager db;

    public AccountDAO(DatabaseManager db) {
        this.db = db;
    }

    public HashMap<String, Account> load(UUID uuid) {
        String sql = "SELECT name, currency, balance FROM wallets WHERE uuid = ?";
        HashMap<String, Account> accounts = new HashMap<>();

        try (Connection conn = db.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    return accounts;
                }

                while (rs.next()) {
                    String name = rs.getString("name");
                    String currency = rs.getString("currency");
                    BigDecimal balance = rs.getBigDecimal("balance");

                    accounts.put(currency, new Account(uuid, name, currency, balance));
                }
            }
        } catch (SQLException ex) {
            SmpxCore.logger().severe("AccountDao.loadAccounts error: " + ex.getMessage());
        }

        return accounts;
    }

    public void create(UUID uuid, Map<String, Account> accounts) {
        if (accounts == null || accounts.isEmpty()) return;

        String sql = "INSERT INTO wallets (uuid, name, currency, balance) VALUES (?, ?, ?, ?)";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Account acc : accounts.values()) {
                ps.setString(1, uuid.toString());
                ps.setString(2, acc.name());
                ps.setString(3, acc.currency());
                ps.setBigDecimal(4, acc.balance());
                ps.addBatch();
            }

            ps.executeBatch();
        } catch (SQLException ex) {
            SmpxCore.logger().severe("AccountDao.createAccounts error: " + ex.getMessage());
        }
    }

    public void save(UUID uuid, Map<String, Account> accounts) {
        if (accounts == null || accounts.isEmpty()) return;

        String sql = "UPDATE wallets SET balance = ? WHERE uuid = ? AND currency = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Account acc : accounts.values()) {
                ps.setBigDecimal(1, acc.balance());
                ps.setString(2, uuid.toString());
                ps.setString(3, acc.currency());
                ps.addBatch();
            }

            ps.executeBatch();
        } catch (SQLException ex) {
            SmpxCore.logger().severe("AccountDao.saveAccounts error: " + ex.getMessage());
        }
    }
}