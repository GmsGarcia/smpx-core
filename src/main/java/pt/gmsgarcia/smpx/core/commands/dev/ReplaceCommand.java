package pt.gmsgarcia.smpx.core.commands.dev;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.commands.SmpxCommand;

public class ReplaceCommand extends SmpxCommand {
    public static final String NAME = "replace";
    public static final String DESCRIPTION = "Replace configurations";
    public static final String PERMISSION = "smpx.dev.replace";

    public ReplaceCommand() {
        super(NAME, PERMISSION);
    }

    @Override
    public void execute(CommandSourceStack source, String @NotNull [] args) {
        CommandSender sender = source.getSender();

        if (sender.hasPermission(PERMISSION)) {
            SmpxCore.replace();
            sender.sendMessage(SmpxCore.messages().component("replace-success", true));
        }
    }
}