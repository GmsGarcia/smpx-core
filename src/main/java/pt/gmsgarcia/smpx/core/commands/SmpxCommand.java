package pt.gmsgarcia.smpx.core.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.user.User;
import pt.gmsgarcia.smpx.core.user.UserMapCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

    protected static CompletableFuture<User> getUser(String name) {
        OfflinePlayer offline = Bukkit.getOfflinePlayer(name);
        return getUser(offline.getUniqueId());
    }

    protected static CompletableFuture<User> getUser(UUID uuid) {
        CompletableFuture<User> future = new CompletableFuture<>();

        SmpxCore.users().get(uuid, new UserMapCallback() {
            @Override
            public void onUserGet(User targetUser) {
                future.complete(targetUser);
            }
        });

        return future;
    }

    protected static CompletableFuture<HashMap<UUID, User>> getUsers(ArrayList<UUID> uuids) {
        CompletableFuture<HashMap<UUID, User>> future = new CompletableFuture<>();

        SmpxCore.users().get(uuids, new UserMapCallback() {
            @Override
            public void onUsersGet(HashMap<UUID, User> users) {
                future.complete(users);
            }
        });

        return future;
    }
}
