package pt.gmsgarcia.smpx.core.storage;

import pt.gmsgarcia.smpx.core.storage.layers.MySQLStorage;

public class StorageManager {
    private IStorageLayer layer;

    public StorageManager() {}

    public void init() {
        this.layer = new MySQLStorage(); // select MySQLStore for now...
        this.layer.init();
    }

    public IStorageLayer layer() {
        return this.layer;
    }
}
