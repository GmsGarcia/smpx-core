package pt.gmsgarcia.smpx.core.user;

public class UserName {
    private final String name;
    private final long lastUsage;

    public UserName(String name, long timestamp) {
        this.name = name;
        this.lastUsage = timestamp;
    }

    public String name() {
        return this.name;
    }

    public long lastUsage() {
        return this.lastUsage;
    }
}
