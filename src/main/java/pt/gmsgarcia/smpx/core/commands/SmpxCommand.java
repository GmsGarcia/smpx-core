package pt.gmsgarcia.smpx.core.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.user.User;

import java.util.UUID;

public abstract class SmpxCommand implements BasicCommand {
    protected final String NAME;
    protected final String PERMISSION;

    protected SmpxCommand(String name, String permission) {
        this.NAME = name;
        this.PERMISSION = permission;
    }

    @Override
    public @Nullable String permission() {
        return PERMISSION;
    }

    protected static User getUser(String name) {
        OfflinePlayer offline = Bukkit.getOfflinePlayer(name);
        return getUser(offline.getUniqueId());
    }

    protected static User getUser(UUID uuid) {
        return SmpxCore.users().get(uuid);
    }
}