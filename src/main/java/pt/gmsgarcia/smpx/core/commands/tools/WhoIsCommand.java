package pt.gmsgarcia.smpx.core.commands.tools;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.commands.SmpxCommand;
import pt.gmsgarcia.smpx.core.user.User;
import pt.gmsgarcia.smpx.core.user.UserName;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class WhoIsCommand extends SmpxCommand {
    public static final String NAME = "whois";
    public static final String DESCRIPTION = "Get a player's info";
    private static final String DEFAULT_PERMISSION = "smpx.tools.whois";
    private static final String ADMIN_PERMISSION = "smpx.tools.whois.*";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public WhoIsCommand() {
        super(NAME, DEFAULT_PERMISSION);
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();

        if (args.length == 0 && source.getSender() instanceof ConsoleCommandSender) {
            sender.sendMessage(SmpxCore.messages().component("invalid-sender", true));
            return;
        }

        String targetName = (args.length > 0) ? args[0] : sender.getName();

        if (!targetName.equals(sender.getName()) && !sender.hasPermission(ADMIN_PERMISSION)) {
            sender.sendMessage(SmpxCore.messages().component("no-permission", true));
            return;
        }

        OfflinePlayer targetOfflinePlayer = Bukkit.getOfflinePlayer(targetName);
        if (!targetOfflinePlayer.hasPlayedBefore() && !targetOfflinePlayer.isOnline()) {
            sender.sendMessage(SmpxCore.messages().component("player-not-found", true, "player", targetName));
            return;
        }

        UUID uuid = targetOfflinePlayer.getUniqueId();

        User user = getUser(uuid);
        if (user == null) {
            sender.sendMessage(SmpxCore.messages().component("generic-error", true));
            return;
        }

        sender.sendMessage(buildMessage(sender, user));
    }

    @Override
    public @NotNull Collection<String> suggest(CommandSourceStack source, String @NotNull [] args) {
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

    private static String buildMessage(CommandSender sender, User target) {
        // convert timestamps to dates
        LocalDateTime lastSeen = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(target.lastSeen()),
                ZoneId.systemDefault()
        );

        LocalDateTime joinDate = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(target.joinDate()),

                ZoneId.systemDefault()
        );

        StringBuilder message = new StringBuilder(target.name() + (sender.hasPermission(ADMIN_PERMISSION) ? " : " + target.uuid() + "\n" : "\n"));
        message.append("Last seen: ").append(lastSeen.format(DATE_TIME_FORMATTER)).append("\n");

        if (sender.hasPermission(ADMIN_PERMISSION)) {
            message.append("Join date: ").append(joinDate.format(DATE_TIME_FORMATTER)).append("\n");
            if (target.previousNames() != null && !target.previousNames().isEmpty()) {
                message.append("Usernames: \n");

                LocalDateTime lastUsage = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(target.previousNames().getFirst().lastUsage()),
                        ZoneId.systemDefault()
                );

                message.append(target.name()).append(" (since ").append(lastUsage.format(DATE_TIME_FORMATTER)).append(")").append("\n");
                for (UserName p : target.previousNames()) {
                    lastUsage = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(p.lastUsage()),
                            ZoneId.systemDefault()
                    );

                    message.append(p.name()).append(" (last usage ").append(lastUsage.format(DATE_TIME_FORMATTER)).append(")").append("\n");
                }
            }
        }

        // remove last newline...
        message = new StringBuilder(message.substring(0, message.length() - 1));

        return message.toString();
    }
}