package pt.gmsgarcia.smpx.core.commands.economy;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.commands.SmpxCommand;
import pt.gmsgarcia.smpx.core.user.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class RemoveBalanceCommand extends SmpxCommand {
    public static final String NAME = "removebalance";
    public static final String DESCRIPTION = "Remove from a player's balance";
    private static final String PERMISSION = "smpx.economy.removebalance";

    public RemoveBalanceCommand() {
        super(NAME, PERMISSION);
    }

    @Override
    public void execute(@NotNull CommandSourceStack source, String @NotNull [] args) {
        CommandSender sender = source.getSender();

        if (sender.hasPermission(PERMISSION)) {
            String targetName = sender.getName();
            BigDecimal amount;

            if (args.length == 0) {
                sender.sendMessage(SmpxCore.messages().component("invalid-arguments-count", true));
                return;
            }

            try {
                if (args.length > 1) {
                    targetName = args[0];
                    amount = new BigDecimal(args[1]);
                } else {
                    amount = new BigDecimal(args[0]);
                }
            } catch (Exception e) {
                sender.sendMessage(SmpxCore.messages().component("generic-error", true));
                e.printStackTrace();
                return;
            }

            OfflinePlayer targetOfflinePlayer = Bukkit.getOfflinePlayer(targetName);
            if (!targetOfflinePlayer.hasPlayedBefore() && !targetOfflinePlayer.isOnline()) {
                sender.sendMessage(SmpxCore.messages().component("player-not-found", true, "player", targetName));
                return;
            }

            CompletableFuture<User> future = getUser(targetName);
            future.thenAccept((target) -> {
                if (target == null) {
                    sender.sendMessage(SmpxCore.messages().component("generic-error", true));
                    return;
                }

                target.addBalance(amount);

                sender.sendMessage(SmpxCore.messages().component("removebalance-sender", true, "receiver", target.name(), "balance", target.balance().toString()));

                if (target.player().isOnline()) {
                    sender.sendMessage(SmpxCore.messages().component("removebalance-receiver", true, "balance", target.balance().toString(), "sender", sender.getName()));
                }
            });
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack source, String @NotNull [] args) {
        CommandSender sender = source.getSender();

        if (sender.hasPermission(PERMISSION)) {
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
