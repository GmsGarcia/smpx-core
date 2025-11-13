package pt.gmsgarcia.smpx.core.listeners;

import pt.gmsgarcia.smpx.core.SmpxCore;

public abstract class SmpxListeners {
    private SmpxListeners() {}

    public static void registerListeners() {
        SmpxCore.instance().getServer().getPluginManager().registerEvents(new PluginListener(), SmpxCore.instance());
        SmpxCore.instance().getServer().getPluginManager().registerEvents(new PlayerListener(), SmpxCore.instance());
        SmpxCore.instance().getServer().getPluginManager().registerEvents(new CommandListener(), SmpxCore.instance());
    }
}