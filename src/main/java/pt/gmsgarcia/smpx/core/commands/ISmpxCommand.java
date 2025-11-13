package pt.gmsgarcia.smpx.core.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.user.User;

import java.util.UUID;

public interface ISmpxCommand extends BasicCommand {
    default User getUser(String name) {
        OfflinePlayer offline = Bukkit.getOfflinePlayer(name);
        return getUser(offline.getUniqueId());
    }

    default User getUser(UUID uuid) {
        return SmpxCore.users().get(uuid);
    }
}