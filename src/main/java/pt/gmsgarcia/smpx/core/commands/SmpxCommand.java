package pt.gmsgarcia.smpx.core.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pt.gmsgarcia.smpx.core.SmpxCore;

import java.util.ArrayList;
import java.util.Locale;

public abstract class SmpxCommand implements BasicCommand {
    protected String NAME;
    protected String DESCRIPTION;

    protected SmpxCommand(String NAME, String DESCRIPTION) {
        this.NAME = NAME;
        this.DESCRIPTION = DESCRIPTION;
    }

    protected boolean hasPermission(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(SmpxCore.messages().component("no-permission", false));
            return false;
        }

        return true;
    }

    protected ArrayList<String> suggestPlayerNames(CommandSender sender, String permission, String @NotNull [] args, int pos) {
        if (!hasPermission(sender, permission)) new ArrayList<>();

        if (args.length == 0) {
            return (ArrayList<String>) Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .toList();
        }

        if (args.length == 1 && (pos >= 0 && pos < args.length)) {
            return (ArrayList<String>) Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(args[pos].toLowerCase(Locale.ROOT)))
                    .toList();
        }

        return new ArrayList<>();
    }
}