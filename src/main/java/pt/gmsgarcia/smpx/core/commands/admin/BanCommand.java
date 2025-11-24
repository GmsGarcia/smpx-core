package pt.gmsgarcia.smpx.core.commands.admin;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.ban.BanListType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.commands.ISmpxCommand;
import pt.gmsgarcia.smpx.core.user.User;

import java.util.*;
import java.util.stream.Collectors;

public class BanCommand implements ISmpxCommand {
    public static final String NAME = "ban";
    public static final String DESCRIPTION = "Ban a player";
    protected static final String PERMISSION = "smpx.admin.ban";

    public BanCommand() {}

    @Override
    public void execute(CommandSourceStack source, String @NotNull [] args) {
        CommandSender sender = source.getSender();

        if (sender.hasPermission(PERMISSION)) {
            if (args.length < 1) {
                sender.sendMessage(SmpxCore.messages().get("invalid-player-argument"));
                return;
            }

            String targetName = args[0];
            User target = SmpxCore.users().get(targetName);
            if (target == null) {
                sender.sendMessage(SmpxCore.messages().component("player-not-found", true));
                sender.sendMessage(SmpxCore.messages().component("generic-error", true));
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

            if (target.isOnline()) {
                target.player().getPlayer().kick(Component.text(reason));
            }

            Date expireDate = null;
            if (duration > 0) {
                expireDate = new Date(System.currentTimeMillis() + (duration * 60L * 60L * 1000L));
            }

            PlayerProfile profile = target.player().getPlayerProfile();
            SmpxCore.instance().getServer().getBanList(BanListType.PROFILE).addBan(profile, reason, expireDate, sender.getName());

            if (expireDate == null) {
                sender.sendMessage(SmpxCore.messages().prefix() + SmpxCore.messages().component("ban-perm", true, "target", target.name(), "reason", reason));
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
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(args[args.length - 1].toLowerCase(Locale.ROOT)))
                    .toList();
            }
        }

        return new ArrayList<>();
    }
}