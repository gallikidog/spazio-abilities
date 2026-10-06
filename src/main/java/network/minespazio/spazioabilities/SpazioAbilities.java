package network.minespazio.spazioabilities;

import network.minespazio.spazioabilities.ability.AbilityManager;
import network.minespazio.spazioabilities.commands.AbilityCommand;
import network.minespazio.spazioabilities.config.ConfigManager;
import network.minespazio.spazioabilities.cooldown.CooldownManager;
import network.minespazio.spazioabilities.gui.AbilityGUI;
import network.minespazio.spazioabilities.hooks.WorldGuardHook;
import network.minespazio.spazioabilities.listeners.AbilityListener;
import network.minespazio.spazioabilities.listeners.CombatTrackerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SpazioAbilities extends JavaPlugin {

    private ConfigManager configManager;
    private WorldGuardHook worldGuardHook;
    private CooldownManager cooldownManager;
    private AbilityManager abilityManager;
    private CombatTrackerListener combatTrackerListener;
    private AbilityGUI abilityGUI;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.configManager = new ConfigManager(this);
        this.worldGuardHook = new WorldGuardHook(this);
        this.cooldownManager = new CooldownManager(this);
        this.abilityManager = new AbilityManager(this);
        this.combatTrackerListener = new CombatTrackerListener(this);
        this.abilityGUI = new AbilityGUI(this);

        // Register listeners
        Bukkit.getPluginManager().registerEvents(this.combatTrackerListener, this);
        Bukkit.getPluginManager().registerEvents(new AbilityListener(this), this);
        Bukkit.getPluginManager().registerEvents(this.abilityGUI, this);

        // Register command
        AbilityCommand cmd = new AbilityCommand(this);
        if (getCommand("spazioabilities") != null) {
            getCommand("spazioabilities").setExecutor(cmd);
            getCommand("spazioabilities").setTabCompleter(cmd);
        }

        getLogger().info("SpazioAbilities v" + getDescription().getVersion() + " activado exitosamente.");
    }

    @Override
    public void onDisable() {
        getLogger().info("SpazioAbilities desactivado.");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public WorldGuardHook getWorldGuardHook() {
        return worldGuardHook;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public AbilityManager getAbilityManager() {
        return abilityManager;
    }

    public CombatTrackerListener getCombatTrackerListener() {
        return combatTrackerListener;
    }

    public AbilityGUI getAbilityGUI() {
        return abilityGUI;
    }
}
