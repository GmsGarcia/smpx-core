package pt.gmsgarcia.smpx.core.commands.dev;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.commands.SmpxCommand;

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
            SmpxCore.reload();
            sender.sendMessage(SmpxCore.messages().component("reload-success", true));
        }
    }
}