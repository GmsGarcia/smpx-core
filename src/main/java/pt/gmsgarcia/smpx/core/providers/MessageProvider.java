package pt.gmsgarcia.smpx.core.providers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.YamlConfiguration;
import pt.gmsgarcia.smpx.core.SmpxCore;

import java.io.File;

public class MessageProvider {
    private YamlConfiguration messages;

    public MessageProvider() {}

    public void load() {
        File file = new File(SmpxCore.instance().getDataFolder(), "messages.yml");

        if (!file.exists()) {
            SmpxCore.instance().saveResource("messages.yml", false);
        }

        messages = new YamlConfiguration();
        messages.options().parseComments(true);

        try {
            messages = YamlConfiguration.loadConfiguration(file);
            SmpxCore.logger().info("Messages loaded");
        } catch (Exception e) {
            SmpxCore.logger().severe("Failed to load messages: " + e.getMessage());
        }
    }

    public void replace() {
        SmpxCore.instance().saveResource("messages.yml", true);
        this.load();
    }

    public String get(String url) {
        String msg = messages.getString("messages."+url);
        if (msg == null) {
            return "Missing message: " + url;
        }

        return msg;
    }

    public String prefix() {
        String prefix = messages.getString("prefix");
        if (prefix == null) {
            prefix = "[smpx-core] ";
        }
        return prefix;
    }

    public Component component(String path, boolean prefix, String... placeholders) {
        String msg = get(path);
        for (int i = 0; i < placeholders.length - 1; i += 2) {
            msg = msg.replace("%" + placeholders[i] + "%", placeholders[i + 1]);
        }
        if (prefix) msg = prefix() + msg;
        return MiniMessage.miniMessage().deserialize(msg);
    }
}