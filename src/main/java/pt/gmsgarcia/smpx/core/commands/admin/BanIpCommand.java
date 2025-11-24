package pt.gmsgarcia.smpx.core.commands.admin;

import io.papermc.paper.ban.BanListType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.commands.ISmpxCommand;
import pt.gmsgarcia.smpx.core.user.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

public class BanIpCommand implements ISmpxCommand {
    public static final String NAME = "ban-ip";
    public static final String DESCRIPTION = "Ban a player by IP";
    protected static final String PERMISSION = "smpx.admin.ban-ip";

    public BanIpCommand() {}

    @Override
    public void execute(CommandSourceStack source, String @NotNull [] args) {
        CommandSender sender = source.getSender();

        if (sender.hasPermission(PERMISSION)) {
            if (args.length < 1) {
                sender.sendMessage(SmpxCore.messages().component("invalid-player-argument", true));
                return;
            }

            String targetName = args[0];
            User target = SmpxCore.users().get(targetName);
            if (target == null) {
                sender.sendMessage(SmpxCore.messages().component("player-not-found", true));
                return;
            }

            int duration = 0;
            if (args.length >= 2) {
                duration = Integer.parseInt(args[1]);
            }

            String reason = SmpxCore.messages().get("no-reason-provided");
            if (args.length >= 2) {
                reason = Arrays.stream(args, 1, args.length)
                        .collect(Collectors.joining(" "));
            }

            if (!target.isOnline()) {
                sender.sendMessage(SmpxCore.messages().component("player-offline", true, "target", target.name()));
                return;
            }

            target.player().getPlayer().kick(Component.text(reason));

            Date expireDate = null;
            if (duration > 0) {
                expireDate = new Date(System.currentTimeMillis() + (duration * 60L * 60L * 1000L));
            }

            SmpxCore.instance().getServer().getBanList(BanListType.IP).addBan(target.player().getPlayer().getAddress().getHostName(), reason, expireDate, sender.getName());

            if (expireDate == null) {
                sender.sendMessage(SmpxCore.messages().component("ban-perm", true, "target", target.name(), "reason", reason));
            } else {
                sender.sendMessage(SmpxCore.messages().component("ban-temp", true, "target", target.name(), "duration", Integer.toString(duration), "reason", reason));
            }
        }
    }

    @Override
    public @NotNull Collection<String> suggest(CommandSourceStack source, String @NotNull [] args) {
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