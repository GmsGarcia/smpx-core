package pt.gmsgarcia.smpx.core.user;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a previous username used by the user.
 */
@SerializableAs("Username")
public class Username implements ConfigurationSerializable {
    private final String name;
    private final long lastUsage;

    public Username(String name, long timestamp) {
        this.name = name;
        this.lastUsage = timestamp;
    }

    /**
     * Returns the username.
     */
    public String name() {
        return this.name;
    }

    /**
     * Returns the last time this username was used.
     */
    public long lastUsage() {
        return this.lastUsage;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", this.name);
        map.put("last-usage", this.lastUsage);
        return map;
    }

    public static Username deserialize(Map<String, Object> map) {
        String name = (String) map.get("name");
        long lastUsage = ((Number) map.get("last-usage")).longValue();
        return new Username(name, lastUsage);
    }
}