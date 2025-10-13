package pt.gmsgarcia.smpx.core.commands.tools;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.commands.CommandsProvider;
import pt.gmsgarcia.smpx.core.commands.SmpxCommand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class HelpCommand extends SmpxCommand {
    public static final String NAME = "help";
    public static final String DESCRIPTION = "Get a commands's info";
    private static final String DEFAULT_PERMISSION = "smpx.tools.help";

    public HelpCommand() {
        super(NAME, DEFAULT_PERMISSION);
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        CommandSender sender = source.getSender();

        if (args.length == 0) {
            sender.sendMessage(buildNamespaceListMessage());
            return;
        }

        // display command list filtered by namespace
        String matchedNamespace = CommandsProvider.getCommandsByNamespace().keySet().stream()
            .filter(k -> k.equalsIgnoreCase(args[0]))
            .findFirst()
            .orElse(null);
        if (matchedNamespace != null) {
            sender.sendMessage(buildCommandListMessage(matchedNamespace));
            return;
        }

        // display command details
        String matchedCommand = CommandsProvider.getCommands().keySet().stream()
            .filter(k -> k.equalsIgnoreCase(args[0]))
            .findFirst()
            .orElse(null);
        if (matchedCommand != null) {
            Command command = CommandsProvider.getCommands().get(matchedCommand);
            sender.sendMessage(buildCommandMessage(command));
            return;
        }

        // display all - default
        sender.sendMessage(buildCommandListMessage(""));
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack source, String @NotNull [] args) {
        Map<String, Map<String, Command>> commandsMap = CommandsProvider.getCommandsByNamespace();

        // return initial suggestions
        if (args.length < 1) {
            return CommandsProvider.getCommands().keySet().stream()
                    .filter(s -> !s.contains(":")) // remove namespace commands
                    .toList();
        }

        // return suggestions while typing
        if (args.length == 1) {
            return commandsMap.keySet().stream()
                    .filter(k -> k.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }

        return new ArrayList<>();
    }

    private Component buildCommandMessage(Command command) {
        Component msg = Component.empty();
        msg = msg.append(SmpxCore.messages().component("help-title-topic", false, "topic", "/"+command.getName())).appendNewline();
        msg = msg.append(SmpxCore.messages().component("help-command-description", false, "description", command.getDescription())).appendNewline();
        msg = msg.append(SmpxCore.messages().component("help-command-usage", false, "usage", command.getUsage()));

        return msg;
    }

    private Component buildNamespaceListMessage() {
        Component msg = Component.empty();
        msg = msg.append(SmpxCore.messages().component("help-title-default", false)).appendNewline();
        msg = msg.append(SmpxCore.messages().component("help-tip", false)).appendNewline();

        int i = 0;
        for (String namespace : CommandsProvider.getNamespaces()) {
            msg = msg.append(SmpxCore.messages().component("help-list-default", false, "topic", namespace));
            i++;

            if (i < CommandsProvider.getNamespaces().size()) {
                msg = msg.appendNewline();
            }
        }

        return msg;
    }

    private Component buildCommandListMessage(String namespace) {
        Component msg = Component.empty();
        Map<String, Command> commands;
        commands = namespace.isEmpty() ? CommandsProvider.getCommands() : CommandsProvider.getCommandsByNamespace().get(namespace);

        if (!namespace.isEmpty()) {
            msg = msg.append(SmpxCore.messages().component("help-title-topic", false, "topic", namespace)).appendNewline();
        } else {
            msg = msg.append(SmpxCore.messages().component("help-title-default", false)).appendNewline();
        }

        msg = msg.append(SmpxCore.messages().component("help-tip", false)).appendNewline();

        int i = 0;
        for (Map.Entry<String, Command> command : commands.entrySet()) {
            i++;

            if (command.getKey().contains(":")) {
                continue;
            }

            Component line = SmpxCore.messages().component("help-list-command", false, "command", command.getKey(), "description", command.getValue().getDescription());

            String plain = PlainTextComponentSerializer.plainText().serialize(line);
            if (plain.length() > 60) {
                String wrapped = command.getValue().getDescription().substring(0, command.getValue().getDescription().length() - (plain.length()-57)) + "...";
                line = SmpxCore.messages().component("help-list-command", false, "command", command.getKey(), "description", wrapped);
            }

            msg = msg.append(line);

            if (i < commands.size()-1) {
                msg = msg.appendNewline();
            }
        }

        return msg;
    }
}