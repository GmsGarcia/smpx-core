package pt.gmsgarcia.smpx.core.storage;

import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.SmpxLogger;
import pt.gmsgarcia.smpx.core.config.SmpxConfig;
import pt.gmsgarcia.smpx.core.storage.layers.H2Storage;
import pt.gmsgarcia.smpx.core.storage.layers.MySQLStorage;
import pt.gmsgarcia.smpx.core.storage.layers.YamlFileStorage;

public class StorageManager {
    private IStorageLayer layer;

    public StorageManager() {}

    public void init() {
        this.layer = switch (SmpxCore.config().storage().type()) {
            case "mysql" -> new MySQLStorage();
            case "h2" -> new H2Storage();
            case "yaml" -> new YamlFileStorage();
            default -> {
                SmpxCore.logger().severe("Invalid storage type " + SmpxCore.config().storage().type() + "! Defaulting to Yaml File Storage...");
                yield new YamlFileStorage();
            }
        };

        this.layer.init();
    }

    public IStorageLayer layer() {
        return this.layer;
    }
}