package pt.gmsgarcia.smpx.core.user;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import it.unimi.dsi.fastutil.Hash;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pt.gmsgarcia.smpx.core.SmpxCore;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * This class handles the loading and caching of User objects.
 */
public class UserMap {
    private final Map<UUID, User> online = new ConcurrentHashMap<>();

    private final LoadingCache<UUID, User> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES) // keep offline users in memory for 10 mins after last use
            .removalListener((RemovalListener<UUID, User>) notification -> {
                if (notification.getValue() != null) {
                    SmpxCore.storage().layer().save(notification.getValue());
                    SmpxCore.logger().info("Saved & removed cached user: " + notification.getKey());
                }
            })
            .build(new CacheLoader<>() {
                @Override
                public @NotNull User load(@NotNull UUID uuid) throws Exception {
                    // Load user from storage when not found in cache
                    User user = SmpxCore.storage().layer().load(uuid);

                    if (user == null) {
                        throw new Exception("User not found for UUID: " + uuid);
                    }

                    return user;
                }
            });

    /* online players load */
    public void load(Player player) {
        UUID uuid = player.getUniqueId();

        if (online.containsKey(uuid)) {
            return;
        }

        User user;

        try {
            user = cache.getUnchecked(uuid);

            if (!user.name().equals(player.getName())) {
                SmpxCore.storage().layer().savePreviousName(user);
                user.updateName(player.getName());
                SmpxCore.storage().layer().save(user);
            }
        } catch (Exception e) {
            user = this.create(uuid);
        }

        online.put(uuid, user);
        SmpxCore.logger().info("Loaded user into online map: " + player.getName());
    }

    /* online players unload */
    public void unload(UUID uuid) {
        User user = online.remove(uuid);

        if (user != null) {
            user.setLastSeen(System.currentTimeMillis());
            cache.put(uuid, user);

            SmpxCore.storage().layer().save(user);

            SmpxCore.logger().info("Unloaded user from online map: " + user.name());
        }
    }

    public User get(UUID uuid) {
        if (online.containsKey(uuid)) {
            return this.online.get(uuid);
        }

        User user;

        try {
            user = cache.getUnchecked(uuid);
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<UUID, User> get(Collection<UUID> uuids) {
        HashMap<UUID, User> users = new HashMap<>();

        for (UUID uuid : uuids) {
            if (online.containsKey(uuid)) {
                users.put(uuid, this.online.get(uuid));
                continue;
            }

            try {
                users.put(uuid, cache.getUnchecked(uuid));
            } catch (Exception e) {
                users.put(uuid, null);
            }
        }

        return users;
    }

    public User create(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        User user = new User(uuid, player.getName(), SmpxCore.config().economy().initialBalance(), System.currentTimeMillis());
        SmpxCore.storage().layer().create(user);

        return user;
    }

    public boolean exists(UUID uuid) {
        if (online.containsKey(uuid)) {
            return true;
        }

        try {
            cache.getUnchecked(uuid);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /* force save */
    public void save(UUID uuid) {
        User user = this.get(uuid);

        if (user != null) {
            SmpxCore.storage().layer().save(user);
        }
    }

    /* force save */
    public void saveAll() {
        online.values().forEach(user -> SmpxCore.storage().layer().save(user));
        cache.asMap().values().forEach(user -> SmpxCore.storage().layer().save(user));
    }

    public void invalidate(UUID uuid) {
        cache.invalidate(uuid);
    }
}