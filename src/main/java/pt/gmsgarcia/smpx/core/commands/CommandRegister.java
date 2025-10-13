package pt.gmsgarcia.smpx.core.commands;

import org.bukkit.command.Command;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.commands.admin.BanCommand;
import pt.gmsgarcia.smpx.core.commands.admin.BanIpCommand;
import pt.gmsgarcia.smpx.core.commands.admin.KickCommand;
import pt.gmsgarcia.smpx.core.commands.dev.ReloadCommand;
import pt.gmsgarcia.smpx.core.commands.dev.ReplaceCommand;
import pt.gmsgarcia.smpx.core.commands.economy.*;
import pt.gmsgarcia.smpx.core.commands.tools.HelpCommand;
import pt.gmsgarcia.smpx.core.commands.dev.SaveCommand;
import pt.gmsgarcia.smpx.core.commands.tools.WhoIsCommand;

import java.util.List;
import java.util.Map;

public class CommandRegister {
    private static Map<String, Command> commands;
    private static List<String> namespaces;
    private static Map<String, Map<String, Command>> commandsByNamespace;

    private CommandRegister() {}

    public static void registerCommands() {
        SmpxCore plugin = SmpxCore.instance();

        // admin
        plugin.registerCommand(KickCommand.NAME, KickCommand.DESCRIPTION, new KickCommand());
        plugin.registerCommand(BanCommand.NAME, BanCommand.DESCRIPTION, new BanCommand());
        plugin.registerCommand(BanIpCommand.NAME, BanIpCommand.DESCRIPTION, new BanIpCommand());

        // economy
        plugin.registerCommand(BalanceCommand.NAME, BalanceCommand.DESCRIPTION, new BalanceCommand());
        plugin.registerCommand(SetBalanceCommand.NAME, SetBalanceCommand.DESCRIPTION, new SetBalanceCommand());
        plugin.registerCommand(AddBalanceCommand.NAME, AddBalanceCommand.DESCRIPTION, new AddBalanceCommand());
        plugin.registerCommand(RemoveBalanceCommand.NAME, RemoveBalanceCommand.DESCRIPTION, new RemoveBalanceCommand());
        plugin.registerCommand(PayCommand.NAME, PayCommand.DESCRIPTION, new PayCommand());

        // tools
        plugin.registerCommand(WhoIsCommand.NAME, WhoIsCommand.DESCRIPTION, new WhoIsCommand());
        plugin.registerCommand(HelpCommand.NAME, HelpCommand.DESCRIPTION, new HelpCommand());

        // dev
        plugin.registerCommand(ReloadCommand.NAME, ReloadCommand.DESCRIPTION, new ReloadCommand());
        plugin.registerCommand(ReplaceCommand.NAME, ReplaceCommand.DESCRIPTION, new ReplaceCommand());
        plugin.registerCommand(SaveCommand.NAME, SaveCommand.DESCRIPTION, new SaveCommand());
    }
}
