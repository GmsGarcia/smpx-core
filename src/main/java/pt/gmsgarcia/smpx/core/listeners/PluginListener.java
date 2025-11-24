package pt.gmsgarcia.smpx.core.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import pt.gmsgarcia.smpx.core.providers.CommandProvider;

public class PluginListener implements Listener {

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        CommandProvider.updateCommands();
    }
}