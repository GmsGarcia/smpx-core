package pt.gmsgarcia.smpx.core.commands.dev;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.commands.SmpxCommand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReloadCommand extends SmpxCommand {
    public static final String NAME = "reload";
    public static final String DESCRIPTION = "Reload configurations";
    public static final String PERMISSION = "smpx.dev.reload";

    public ReloadCommand() {
        super(NAME, PERMISSION);
    }

    @Override
    public void execute(CommandSourceStack source, String @NotNull [] args) {
        CommandSender sender = source.getSender();

        if (sender.hasPermission(PERMISSION)) {
            String module = "all";

            if (args.length != 0) {
                module = args[0];
            }

            switch (module) {
                case "all" -> {
                    SmpxCore.config().load();
                    SmpxCore.messages().load();
                }
                case "config" -> SmpxCore.config().load();
                case "messages" -> SmpxCore.messages().load();
                default -> {
                    sender.sendMessage(SmpxCore.messages().component("invalid-module", true));
                    return;
                }
            }

            if (module.equals("all")) {
                sender.sendMessage(SmpxCore.messages().component("reload-all-success", true ));
            } else {
                sender.sendMessage(SmpxCore.messages().component("reload-success", true, "module", module));
            }
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack source, String @NotNull [] args) {
        CommandSender sender = source.getSender();
        List<String> modules = List.of("all", "config", "messages");

        if (sender.hasPermission(PERMISSION)) {
            if (args.length == 0) {
                return modules;
            }

            if (args.length == 1) {
                return modules.stream().filter(name -> name.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).toList();
            }
        }

        return new ArrayList<>();
    }
}