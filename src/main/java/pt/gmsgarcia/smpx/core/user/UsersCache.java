package pt.gmsgarcia.smpx.core.user;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pt.gmsgarcia.smpx.core.SmpxCore;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * This class handles the loading and caching of {@link User} objects.
 */
public class UsersCache {
    private final Map<UUID, User> online = new ConcurrentHashMap<>();

    private final LoadingCache<UUID, User> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES) // keep offline users in memory for 10 mins after last use
            .removalListener((RemovalListener<UUID, User>) notification -> {
                if (notification.getValue() != null) {
                    SmpxCore.storage().layer().saveUser(notification.getValue());
                    SmpxCore.logger().info("Saved & removed cached user: " + notification.getKey());
                }
            })
            .build(new CacheLoader<>() {
                @Override
                public @NotNull User load(@NotNull UUID uuid) throws Exception {
                    // Load user from storage when not found in cache
                    User user = SmpxCore.storage().layer().getUser(uuid);

                    if (user == null) {
                        throw new Exception("User not found for UUID: " + uuid);
                    }

                    return user;
                }
            });

    /**
     * Loads the user with the specified {@link UUID} in the online map.
     */
    public void load(Player player) {
        UUID uuid = player.getUniqueId();

        if (online.containsKey(uuid)) {
            return;
        }

        User user;

        try {
            user = cache.getUnchecked(uuid);

            if (!user.name().equals(player.getName())) {
                SmpxCore.storage().layer().createPreviousUsername(user);
                user.updateName(player.getName());
            }
        } catch (UncheckedExecutionException e) {
            user = this.create(uuid);
        }

        online.put(uuid, user);
        SmpxCore.logger().info("Loaded user into online map: " + player.getName());
    }

    /**
     * Unloads the user with the specified {@link UUID} from the online map
     * and put it in {@code cache} (for 10 minutes).
     */
    public void unload(UUID uuid) {
        User user = online.remove(uuid);
        if (user != null) {
            user.setLastSeen(System.currentTimeMillis());
            cache.put(uuid, user);
            SmpxCore.logger().info("Unloaded user from online map: " + user.name());
        }
    }

    /**
     * Invalidates the user with the specified {@link UUID} in cache.
     * If no {@link UUID} is provided ({@code null}), it will proceed
     * to invalidate all users in cache.
     */
    public void invalidate(UUID uuid) {
        if (uuid == null) {
            cache.invalidateAll();
        } else {
            cache.invalidate(uuid);
        }
    }

    /**
     * Returns the user with the specified {@link UUID}. If no value is
     * found returns {@code null}.
     */
    public User get(UUID uuid) {
        User user = online.get(uuid);
        if (user != null) return user;

        try {
            return cache.getUnchecked(uuid);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Creates a new user with the specified {@link UUID}.
     */
    public User create(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        User user = User.build(uuid, player.getName());
        SmpxCore.storage().layer().createUser(user);

        return user;
    }

    /**
     * Forces a save for the user with the specified {@link UUID}.
     * If no {@link UUID} is provided ({@code null}), it will proceed
     * to save all users in the online map and cache.
     */
    public void save(UUID uuid) {
        if (uuid == null) {
            online.values().forEach(user -> SmpxCore.storage().layer().saveUser(user));
            cache.asMap().values().forEach(user -> SmpxCore.storage().layer().saveUser(user));
            SmpxCore.accounts().save(null);
            return;
        }

        User user = this.get(uuid);
        if (user != null) {
            SmpxCore.storage().layer().saveUser(user);
            SmpxCore.accounts().save(uuid);
        }
    }
}