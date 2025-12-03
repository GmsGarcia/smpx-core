package pt.gmsgarcia.smpx.core.user;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.account.Account;
import pt.gmsgarcia.smpx.core.config.CurrencyConfig;

import java.util.*;

/**
 * This class represents a player and its data. It contains all the
 * information that is stored for a player, such as their UUID,
 * name, balance, joinDate, lastSeen, and previousNames.
 */
@SerializableAs("User")
public class User implements ConfigurationSerializable {
    private final OfflinePlayer player;
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
        if (accounts == null || accounts.isEmpty()) {
            this.accounts = new HashMap<>();

            for (Map.Entry<String, CurrencyConfig> currency : SmpxCore.config().economy().currencies().entrySet()) {
                this.accounts.put(currency.getKey(), new Account(uuid, name, currency.getKey(), currency.getValue().initialBalance()));
            }

            SmpxCore.storage().layer().createAccounts(uuid, this.accounts());
        } else {
            this.accounts = accounts;
        }

        this.player = Bukkit.getOfflinePlayer(uuid);
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
     * Returns the user {@link OfflinePlayer} object.
     */
    public OfflinePlayer player() {
        return this.player;
    }

    /**
     * Checks if this player is online.
     */
    public boolean isOnline() {
        return this.player.isOnline();
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

    /**
     * Serialize User object to store in YAML file.
     */
    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", uuid.toString());
        map.put("name", name);
        map.put("join-date", joinDate);
        map.put("last-seen", lastSeen);
        map.put("previous-names", previousNames);
        return map;
    }

    /**
     * Deserialize from YAML file to User object.
     */
    @SuppressWarnings("unchecked")
    public static User deserialize(Map<String, Object> map) {
        UUID uuid = UUID.fromString((String) map.get("uuid"));
        String name = (String) map.get("name");
        long joinDate = ((Number) map.get("join-date")).longValue();
        long lastSeen = ((Number) map.get("last-seen")).longValue();

        ArrayList<Username> previousNames = new ArrayList<>();
        Object raw = map.get("previous-names");

        if (raw instanceof List<?>) {
            for (Object o : (List<?>) raw) {
                if (o instanceof Username u) previousNames.add(u);
                else if (o instanceof Map<?,?> data)
                    previousNames.add(Username.deserialize((Map<String, Object>) data));
            }
        }

        return User.build(uuid, name, joinDate, lastSeen, previousNames);
    }
}