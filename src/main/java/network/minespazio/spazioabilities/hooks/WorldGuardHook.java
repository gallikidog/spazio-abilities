package network.minespazio.spazioabilities.hooks;

import network.minespazio.spazioabilities.SpazioAbilities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WorldGuardHook {

    private final SpazioAbilities plugin;
    private boolean enabled = false;

    public WorldGuardHook(SpazioAbilities plugin) {
        this.plugin = plugin;
        if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            this.enabled = true;
            plugin.getLogger().info("WorldGuard hook cargado exitosamente.");
        } else {
            plugin.getLogger().info("WorldGuard no encontrado. Las verificaciones PvP usarán el estado nativo del mundo.");
        }
    }

    public boolean isPvPAllowed(Player player) {
        if (player == null) return false;

        if (!plugin.getConfig().getBoolean("worldguard.enabled", true) || !enabled) {
            return player.getWorld().getPVP();
        }

        try {
            com.sk89q.worldedit.util.Location loc = com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(player.getLocation());
            com.sk89q.worldguard.protection.regions.RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
            com.sk89q.worldguard.protection.regions.RegionQuery query = container.createQuery();

            com.sk89q.worldguard.LocalPlayer localPlayer = com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst().wrapPlayer(player);
            return query.testState(loc, localPlayer, com.sk89q.worldguard.protection.flags.Flags.PVP);
        } catch (Throwable t) {
            return player.getWorld().getPVP();
        }
    }

    public boolean canUseAbility(Player attacker, Player target) {
        if (attacker == null) return false;

        // For self-targeted abilities
        if (target == null) {
            return isPvPAllowed(attacker);
        }

        // Both players MUST be in a region where PvP is allowed!
        boolean attackerInPvP = isPvPAllowed(attacker);
        boolean targetInPvP = isPvPAllowed(target);

        return attackerInPvP && targetInPvP;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
