package pt.gmsgarcia.smpx.core.commands.economy;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.commands.SmpxCommand;
import pt.gmsgarcia.smpx.core.user.User;
import pt.gmsgarcia.smpx.core.user.UserMapCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BalanceCommand extends SmpxCommand {
    public static final String NAME = "balance";
    public static final String DESCRIPTION = "Get a player's balance";
    private static final String DEFAULT_PERMISSION = "smpx.economy.balance";
    private static final String ADMIN_PERMISSION = "smpx.economy.balance.*";

    public BalanceCommand() {
        super(NAME, DEFAULT_PERMISSION);
    }

    @Override
    public void execute(CommandSourceStack source, String @NotNull [] args) {
        CommandSender sender = source.getSender();

        if (sender.hasPermission(DEFAULT_PERMISSION)) {
            String targetName = sender.getName();

            if (args.length != 0) {
                if (sender.hasPermission(ADMIN_PERMISSION)) {
                    targetName = args[0];

                    OfflinePlayer targetOfflinePlayer = Bukkit.getOfflinePlayer(targetName);
                    if (!targetOfflinePlayer.hasPlayedBefore() && !targetOfflinePlayer.isOnline()) {
                        sender.sendMessage(SmpxCore.messages().component("player-not-found", true, "player", targetName));
                        return;
                    }
                }
            }

            CompletableFuture<User> future = getUser(targetName);
            future.thenAccept((target) -> {
                if (target == null) {
                    sender.sendMessage(SmpxCore.messages().component("generic-error", true));
                    return;
                }

                sender.sendMessage(SmpxCore.messages().component("balance", true, "balance", target.balance().toString()));
            });
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack source, String @NotNull [] args) {
        CommandSender sender = source.getSender();

        if (sender.hasPermission(ADMIN_PERMISSION)) {
            if (args.length == 0) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            }

            if (args.length == 1) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                        .toList();
            }
        }

        return new ArrayList<>();
    }
}