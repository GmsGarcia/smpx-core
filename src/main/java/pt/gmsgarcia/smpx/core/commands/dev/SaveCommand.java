package pt.gmsgarcia.smpx.core.commands.dev;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.commands.ISmpxCommand;

public class SaveCommand implements ISmpxCommand {
    public static final String NAME = "save";
    public static final String DESCRIPTION = "Save users in cache to database";
    public static final String PERMISSION = "smpx.dev.save";

    public SaveCommand() {}

    @Override
    public void execute(CommandSourceStack source, String @NotNull [] args) {
        CommandSender sender = source.getSender();

        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(SmpxCore.messages().component("no-permission", false));
            return;
        }

        SmpxCore.users().save(null);
        sender.sendMessage(SmpxCore.messages().component("save-success", true));
    }
}