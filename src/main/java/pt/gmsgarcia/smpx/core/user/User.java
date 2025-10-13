package pt.gmsgarcia.smpx.core.user;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Represents persistent player data.
 * <p>
 * This class contains all the information that is stored for a player,
 * such as their UUID, name, balance, joinDate, lastSeen, and previousNames.
 */
public class User {
    private final Player player;
    private final UUID uuid;
    private String name;
    private BigDecimal balance;
    private final long joinDate;
    private long lastSeen;
    private ArrayList<UserName> previousNames;

    public User(UUID uuid, String name, BigDecimal balance, long joinDate, long lastSeen, ArrayList<UserName> previousNames) {
        this.uuid = uuid;
        this.name = name;
        this.balance = balance;
        this.joinDate = joinDate;
        this.lastSeen = lastSeen;
        this.previousNames = previousNames;

        OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
        if (offline.isOnline()) {
            this.player = Bukkit.getPlayer(uuid);
        } else {
            this.player = Bukkit.getPlayer(uuid); // TODO: make a wrapper for offlineplayer
        }
    }

    /**
     * this constructor is mostly used when creating new users (players first time joining...)
     */
    public User(UUID uuid, String name, BigDecimal balance, long joinDate) {
        this.uuid = uuid;
        this.name = name;
        this.balance = balance;
        this.joinDate = joinDate;

        OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
        if (offline.isOnline()) {
            this.player = Bukkit.getPlayer(uuid);
        } else {
            this.player = Bukkit.getPlayer(uuid); // TODO: make a wrapper for offlineplayer
        }
    }

    public Player player() {
        return this.player;
    }

    public UUID uuid() {
        return this.uuid;
    }

    public String name() {
        return this.name;
    }

    public BigDecimal balance() {
        return this.balance;
    }

    public long joinDate() {
        return this.joinDate;
    }

    public long lastSeen() {
        return this.lastSeen;
    }

    public ArrayList<UserName> previousNames() {
        return this.previousNames;
    }

    public void updateName(String name) {
        if (this.previousNames == null) {
            this.previousNames = new ArrayList<>();
        }

        this.previousNames.add(new UserName(this.name, this.lastSeen));
        this.name = name;
    }

    public void setLastSeen(long timestamp) {
        this.lastSeen = timestamp;
    }

    public void setBalance(BigDecimal amount) {
        this.balance = amount;
    }

    public void addBalance(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

    public void removeBalance(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            return;
        }

        this.balance = this.balance.subtract(amount);
    }

    public boolean canAfford(BigDecimal value) {
        return this.balance.compareTo(value) >= 0;
    }

    public void pay(User user, BigDecimal amount) {
        this.removeBalance(amount);
        user.addBalance(amount);
    }
}