package pt.gmsgarcia.smpx.core.commands.admin;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.commands.SmpxCommand;
import pt.gmsgarcia.smpx.core.user.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

public class KickCommand extends SmpxCommand {
    public static final String NAME = "kick";
    public static final String DESCRIPTION = "Kick a player";
    protected static final String PERMISSION = "smpx.admin.kick";

    public KickCommand() {
        super(NAME, DESCRIPTION);
    }

    @Override
    public void execute(CommandSourceStack source, String @NotNull [] args) {
        CommandSender sender = source.getSender();
        if (!hasPermission(sender, PERMISSION)) return;

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

        String reason = SmpxCore.messages().get("no-reason-provided");
        if (args.length >= 2) {
            reason = Arrays.stream(args, 1, args.length)
                    .collect(Collectors.joining(" "));
        }

        if (!target.isOnline()) {
            sender.sendMessage(SmpxCore.messages().component("player-offline", true, "target", target.name()));
            return;
        }

        sender.sendMessage(SmpxCore.messages().component("kick", true, "target", target.name(), "reason", reason));
    }

    @Override
    public @NotNull Collection<String> suggest(CommandSourceStack source, String @NotNull [] args) {
        CommandSender sender = source.getSender();
        return suggestPlayerNames(sender, PERMISSION, args, args.length-1); // TODO: is this the correct position?
    }
}