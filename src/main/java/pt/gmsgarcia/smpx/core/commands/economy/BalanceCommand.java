package pt.gmsgarcia.smpx.core.commands.economy;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.account.Account;
import pt.gmsgarcia.smpx.core.commands.SmpxCommand;
import pt.gmsgarcia.smpx.core.user.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public class BalanceCommand extends SmpxCommand {
    public static final String NAME = "balance";
    public static final String DESCRIPTION = "Get a player's balance";
    private static final String DEFAULT_PERMISSION = "smpx.economy.balance";
    private static final String ADMIN_PERMISSION = "smpx.economy.balance.*";

    public BalanceCommand() {
        super(NAME, DESCRIPTION);
    }

    @Override
    public void execute(CommandSourceStack source, String @NotNull [] args) {
        CommandSender sender = source.getSender();
        if (!hasPermission(sender, DEFAULT_PERMISSION)) return;

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

        User target = SmpxCore.users().get(targetName);
        if (target == null) {
            // target has played before (or is online) but no User object was found... o-o
            sender.sendMessage(SmpxCore.messages().component("generic-error", true));
            return;
        }

        Account account = target.account();
        if (account == null) {
            // target exists but no Account objects were found... o-o
            sender.sendMessage(SmpxCore.messages().component("generic-error", true));
            return;
        }

        sender.sendMessage(SmpxCore.messages().component("balance", true, "balance", account.balance().toString()));
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack source, String @NotNull [] args) {
        CommandSender sender = source.getSender();
        return suggestPlayerNames(sender, ADMIN_PERMISSION, args, args.length-1); // TODO: is this the correct position?
    }
}