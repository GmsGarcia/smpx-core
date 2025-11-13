package pt.gmsgarcia.smpx.core.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.TabCompleteEvent;
import pt.gmsgarcia.smpx.core.SmpxCore;

public class CommandListener implements Listener {
    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        SmpxCore.logger().info("ahh");
    }
}