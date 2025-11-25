package pt.gmsgarcia.smpx.core.commands.dev;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.commands.SmpxCommand;

public class SaveCommand extends SmpxCommand {
    public static final String NAME = "save";
    public static final String DESCRIPTION = "Save users in cache to database";
    public static final String PERMISSION = "smpx.dev.save";

    public SaveCommand() {
        super(NAME, DESCRIPTION);
    }

    @Override
    public void execute(CommandSourceStack source, String @NotNull [] args) {
        CommandSender sender = source.getSender();
        if (!hasPermission(sender, PERMISSION)) return;

        SmpxCore.users().save(null);
        sender.sendMessage(SmpxCore.messages().component("save-success", true));
    }
}