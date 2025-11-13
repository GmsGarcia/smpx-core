package pt.gmsgarcia.smpx.core.account;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.config.CurrencyConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * This class handles the loading and caching of {@link Account} objects.
 * <br>
 * <br>
 * <b>Warning</b>: Both caches hold account object references when an account's
 * user is online. This is perfectly fine since they only hold *references*.
 */
public class AccountsCache {
    private final Map<UUID, HashMap<String, Account>> online = new HashMap<>();

    private final LoadingCache<UUID, HashMap<String, Account>> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .removalListener((RemovalListener<UUID, HashMap<String, Account>>) notif -> {
                if (notif.getValue() != null) {
                    SmpxCore.storage().layer().saveAccounts(notif.getKey(), notif.getValue());
                    SmpxCore.logger().info("Saved & removed cached accounts with UUID " + notif.getKey());
                }
            })
            .build(new CacheLoader<>() {
                @Override
                public @NotNull HashMap<String, Account> load(@NotNull UUID uuid) throws Exception {
                    HashMap<String, Account> accounts = SmpxCore.storage().layer().getAccounts(uuid);

                    if (accounts == null || accounts.isEmpty()) {
                        throw new Exception("Accounts not found for UUID: " + uuid);
                    }

                    return accounts;
                }
            });

    /**
     * Loads the accounts with the specified {@link UUID} in the online map.
     */
    public void load(UUID uuid) {
        if (online.containsKey(uuid)) {
            return;
        }

        HashMap<String, Account> accounts;

        try {
            accounts = cache.getUnchecked(uuid);
        } catch (UncheckedExecutionException e) {
            SmpxCore.logger().info("No accounts found with UUID " + uuid + ". Creating...");
            accounts = this.create(uuid);
        }

        online.put(uuid, accounts);
        SmpxCore.logger().info("Loaded accounts with UUID " + uuid + " into online map");
    }

    /**
     * Unloads the accounts with the provided {@link UUID} from the online map
     * and put them in {@code cache} (for 10 minutes).
     */
    public void unload(UUID uuid) {
        HashMap<String, Account> accounts = online.remove(uuid);
        if (accounts != null) {
            cache.put(uuid, accounts);
            SmpxCore.logger().info("Unloaded accounts with UUID " + uuid);
        }
    }

    /**
     * Invalidates the accounts with the specified {@link UUID} in cache.
     * If no {@link UUID} is provided ({@code null}), it will proceed
     * to invalidate all accounts in cache.
     */
    public void invalidate(UUID uuid) {
        if (uuid == null) {
            cache.invalidateAll();
        } else {
            cache.invalidate(uuid);
        }
    }

    /**
     * Returns the accounts with the specified {@link UUID}. If no value is
     * found returns {@code null}.
     */
    public HashMap<String, Account> get(UUID uuid) {
        HashMap<String, Account> accounts = online.get(uuid);
        if (accounts != null) return accounts;

        try {
            return cache.getUnchecked(uuid);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Creates new accounts for the specified {@link UUID}.
     */
    public HashMap<String, Account> create(UUID uuid) {
        return this.create(uuid, Bukkit.getOfflinePlayer(uuid).getName());
    }

    /**
     * Creates new accounts for the specified {@link UUID} with the given name.
     */
    public HashMap<String, Account> create(UUID uuid, String name) {
        HashMap<String, Account> accounts = new HashMap<>();

        // populate accounts map with default values
        for (Map.Entry<String, CurrencyConfig> currency : SmpxCore.config().economy().currencies().entrySet()) {
            accounts.put(currency.getKey(), new Account(uuid, name, currency.getKey(), currency.getValue().initialBalance()));
        }

        SmpxCore.storage().layer().createAccounts(uuid, accounts);

        return accounts;
    }

    /**
     * Forces a save for the accounts with the specified {@link UUID}.
     * If no {@link UUID} is provided ({@code null}), it will proceed
     * to save every account in the online map and cache.
     */
    public void save(UUID uuid) {
        if (uuid == null) {
            // TODO: i gotta rework this later...
            online.values().forEach(accounts -> SmpxCore.storage().layer().saveAccounts(accounts.get("default").uuid(), accounts));
            cache.asMap().values().forEach(accounts -> SmpxCore.storage().layer().saveAccounts(accounts.get("default").uuid(), accounts));
            return;
        }

        HashMap<String, Account> accounts = this.get(uuid);
        if (accounts != null) {
            SmpxCore.storage().layer().saveAccounts(uuid, accounts);
        }
    }
}