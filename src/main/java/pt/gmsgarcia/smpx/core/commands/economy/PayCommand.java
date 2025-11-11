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
import java.util.UUID;

public class PayCommand extends SmpxCommand {
    public static final String NAME = "pay";
    public static final String DESCRIPTION = "Get a player's balance";
    private static final String PERMISSION = "smpx.economy.pay";

    public PayCommand() {
        super(NAME, PERMISSION);
    }

    @Override
    public void execute(@NotNull CommandSourceStack source, String @NotNull [] args) {
        CommandSender sender = source.getSender();

        if (sender.hasPermission(PERMISSION)) {
            if (!(sender instanceof Player) || ((Player) sender).getPlayer() == null) {
                sender.sendMessage(SmpxCore.messages().component("invalid-sender", true));
                return;
            }

            if (args.length != 2) {
                sender.sendMessage(SmpxCore.messages().component("invalid-arguments-count", true));
                return;
            }

            String receiverName;
            BigDecimal amount;

            try {
                receiverName = args[0];
                amount = new BigDecimal(args[1]);
            } catch (Exception e) {
                sender.sendMessage(SmpxCore.messages().component("generic-error", true));
                e.printStackTrace();
                return;
            }

            OfflinePlayer receiverOfflinePlayer = Bukkit.getOfflinePlayer(receiverName);
            if (!receiverOfflinePlayer.hasPlayedBefore() && !receiverOfflinePlayer.isOnline()) {
                sender.sendMessage(SmpxCore.messages().component("player-not-found", true, "player", receiverName));
                return;
            }

            UUID senderUUID = ((Player) sender).getPlayer().getUniqueId();
            UUID receiverUUID = receiverOfflinePlayer.getUniqueId();

            User senderUser = getUser(senderUUID);
            if (senderUser == null) {
                sender.sendMessage(SmpxCore.messages().component("generic-error", true));
                return;
            }

            User receiverUser = getUser(receiverUUID);
            if (receiverUser == null) {
                sender.sendMessage(SmpxCore.messages().component("generic-error", true));
                return;
            }

            if (!senderUser.canAfford(amount)) {
                sender.sendMessage(SmpxCore.messages().component("cant-afford", true));
                return;
            }

            senderUser.pay(receiverUser, amount);

            sender.sendMessage(SmpxCore.messages().component("pay-sender", true, "amount", amount.toString(), "receiver", receiverUser.name()));

            if (receiverOfflinePlayer.isOnline()) {
                Player receiverPlayer = receiverOfflinePlayer.getPlayer();
                if (receiverPlayer != null) {
                    sender.sendMessage(SmpxCore.messages().component("pay-receiver", true, "amount", amount.toString(), "sender", senderUser.name()));
                }
            }
        }
    }
}