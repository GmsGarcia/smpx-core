package pt.gmsgarcia.smpx.core.user;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.account.Account;
import pt.gmsgarcia.smpx.core.config.CurrencyConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class represents a player and its data. It contains all the
 * information that is stored for a player, such as their UUID,
 * name, balance, joinDate, lastSeen, and previousNames.
 */
public class User {
    private final Player player;
    private final UUID uuid;
    private String name;
    private final long joinDate;
    private long lastSeen;

    private final HashMap<String, Account> accounts;
    private final ArrayList<Username> previousNames;

    private User(UUID uuid, String name, long joinDate, long lastSeen, ArrayList<Username> previousNames) {
        this.uuid = uuid;
        this.name = name;
        this.joinDate = joinDate;
        this.lastSeen = lastSeen;

        this.previousNames = previousNames != null ? previousNames : new ArrayList<>();

        // load accounts
        HashMap<String, Account> accounts = SmpxCore.accounts().get(uuid);

        // create accounts if not found...
        if (accounts == null) {
            this.accounts = new HashMap<>();

            for (Map.Entry<String, CurrencyConfig> currency : SmpxCore.config().economy().currencies().entrySet()) {
                this.accounts.put(currency.getKey(), new Account(uuid, name, currency.getKey(), currency.getValue().initialBalance()));
            }

            SmpxCore.storage().layer().createAccounts(uuid, this.accounts());
        } else {
            this.accounts = accounts;
        }

        // TODO: this might return null if the player is offline...
        this.player = Bukkit.getPlayer(uuid);
    }

    /**
     * Builds a new {@link User} object.
     * This method if mostly used when creating
     * a new user (first time joining).
     */
    public static User build(UUID uuid, String name) {
        return User.build(uuid, name, System.currentTimeMillis(), -1, null);
    }

    /**
     * Builds a new {@link User} object.
     * This method is mostly used when loading
     * user data from storage.
     */
    public static User build(UUID uuid, String name, long joinDate, long lastSeen, ArrayList<Username> previousNames) {
        return new User(uuid, name, joinDate, lastSeen, previousNames);
    }

    /**
     * Returns the user {@link Player} object.
     */
    public Player player() {
        return this.player;
    }

    /**
     * Returns the user {@link UUID}.
     */
    public UUID uuid() {
        return this.uuid;
    }

    /**
     * Returns the username.
     */
    public String name() {
        return this.name;
    }

    /**
     * Returns the default {@link Account} object.
     */
    public Account account() {
        return this.accounts.get("default");
    }

    /**
     * Returns the specified {@link Account} object.
     */
    public Account account(String name) {
        return this.accounts.get(name);
    }

    /**
     * Returns an {@link HashMap} containing all the {@link Account} objects.
     */
    public HashMap<String, Account> accounts() {
        return this.accounts;
    }

    /**
     * Returns the user join date.
     */
    public long joinDate() {
        return this.joinDate;
    }

    /**
     * Returns the user 'last seen' date.
     */
    public long lastSeen() {
        return this.lastSeen;
    }

    /**
     * Returns an {@link ArrayList} containing all the previous {@link Username} objects.
     */
    public ArrayList<Username> previousNames() {
        return this.previousNames;
    }

    /**
     * Updates the username.
     */
    public void updateName(String name) {
        this.previousNames.add(new Username(this.name, this.lastSeen));
        this.name = name;
    }

    /**
     * Updates the 'last seen' date.
     */
    public void setLastSeen(long timestamp) {
        this.lastSeen = timestamp;
    }
}