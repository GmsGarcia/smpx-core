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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public class AddBalanceCommand extends SmpxCommand {
    public static final String NAME = "addbalance";
    public static final String DESCRIPTION = "Add to a player's balance";
    private static final String PERMISSION = "smpx.economy.addbalance";

    public AddBalanceCommand() {
        super(NAME, DESCRIPTION);
    }

    @Override
    public void execute(@NotNull CommandSourceStack source, String @NotNull [] args) {
        CommandSender sender = source.getSender();
        if (!hasPermission(sender, PERMISSION)) return;

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

        User target = SmpxCore.users().get(targetName);
        if (target == null) {
            // receiver has played before (or is online for the first time) but no User object was found... o-o
            sender.sendMessage(SmpxCore.messages().component("generic-error", true));
            return;
        }

        Account account = target.account();
        if (account == null) {
            // receiver exists but no Account objects were found... o-o
            sender.sendMessage(SmpxCore.messages().component("generic-error", true));
            return;
        }

        account.deposit(amount);

        sender.sendMessage(SmpxCore.messages().component("addbalance-sender", true, "receiver", target.name(), "balance", account.balance().toString()));

        if (target.player().isOnline()) {
            sender.sendMessage(SmpxCore.messages().component("addbalance-receiver", true, "balance", account.balance().toString(), "sender", sender.getName()));
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack source, String @NotNull [] args) {
        CommandSender sender = source.getSender();
        return suggestPlayerNames(sender, PERMISSION, args, args.length-1); // TODO: is this the correct position?
    }
}