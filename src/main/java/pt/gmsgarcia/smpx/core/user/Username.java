package pt.gmsgarcia.smpx.core.user;

/**
 * This class represents a previous username used by the user.
 */
public class Username {
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
}