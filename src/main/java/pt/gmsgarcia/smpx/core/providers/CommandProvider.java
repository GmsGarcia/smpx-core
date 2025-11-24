package pt.gmsgarcia.smpx.core.providers;

import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import pt.gmsgarcia.smpx.core.SmpxCore;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandProvider {
    private static Map<String, Command> commands;
    private static List<String> namespaces;
    private static Map<String, Map<String, Command>> commandsByNamespace;

    private CommandProvider() {}

    public static void updateCommands() {
        commands = new HashMap<>();
        namespaces = new ArrayList<>();
        commandsByNamespace = new HashMap<>();

        Map<String, Command> known = SmpxCore.instance().getServer().getCommandMap().getKnownCommands();

        for (Map.Entry<String, Command> set : known.entrySet()) {
            String namespace = extractNamespace(set);

            if (!namespaces.contains(namespace)) {
                namespaces.add(namespace);
                commandsByNamespace.put(namespace, new HashMap<>());
            }

            commands.put(set.getKey(), set.getValue());
            commandsByNamespace.get(namespace).put(set.getKey(), set.getValue());
        }
    }

    public static Map<String, Command> getCommands() {
        return commands;
    }

    public static List<String> getNamespaces() {
        return namespaces;
    }

    public static Map<String, Map<String, Command>> getCommandsByNamespace() {
        return commandsByNamespace;
    }

    private static String extractNamespace(Map.Entry<String, Command> set) {
        Command command = set.getValue();
        String className = command.getClass().getName();

        String namespace = "unknown";

        // get namespace
        if (command instanceof PluginCommand pluginCommand) {
            namespace = pluginCommand.getPlugin().getName();
        } else if (className.startsWith("org.bukkit.craftbukkit.command.VanillaCommandWrapper")) {
            try {
                Field helpCommandNamespaceField = command.getClass().getDeclaredField("helpCommandNamespace");
                helpCommandNamespaceField.setAccessible(true);
                namespace = (String) helpCommandNamespaceField.get(command);
            } catch (Exception ignored) {
            }
        } else if (className.startsWith("io.papermc.paper.command.brigadier.PluginVanillaCommandWrapper")) {
            try {
                Field pluginField = command.getClass().getDeclaredField("plugin");
                pluginField.setAccessible(true);
                Plugin plugin = (Plugin) pluginField.get(command);
                namespace = plugin.getName();
            } catch (Exception ignored) {
            }
        }
        return namespace;
    }
}