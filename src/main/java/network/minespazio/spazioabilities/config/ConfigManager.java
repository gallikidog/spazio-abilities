package network.minespazio.spazioabilities.config;

import network.minespazio.spazioabilities.SpazioAbilities;
import org.bukkit.ChatColor;

public class ConfigManager {

    private final SpazioAbilities plugin;

    public ConfigManager(SpazioAbilities plugin) {
        this.plugin = plugin;
    }

    public String color(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public String getMsg(String path, String def) {
        String prefix = plugin.getConfig().getString("prefix", "&7[&bSpazioAbilities&7] ");
        String msg = plugin.getConfig().getString(path, def);
        return color(msg.replace("{prefix}", prefix));
    }
}
