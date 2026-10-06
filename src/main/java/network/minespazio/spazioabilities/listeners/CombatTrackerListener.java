package network.minespazio.spazioabilities.listeners;

import network.minespazio.spazioabilities.SpazioAbilities;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatTrackerListener implements Listener {

    private final SpazioAbilities plugin;
    private final Map<UUID, AttackerInfo> lastAttackers = new HashMap<>();

    public CombatTrackerListener(SpazioAbilities plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player victim = (Player) event.getEntity();

        Player attacker = null;
        if (event.getDamager() instanceof Player) {
            attacker = (Player) event.getDamager();
        } else if (event.getDamager() instanceof org.bukkit.entity.Projectile) {
            org.bukkit.entity.Projectile proj = (org.bukkit.entity.Projectile) event.getDamager();
            if (proj.getShooter() instanceof Player) {
                attacker = (Player) proj.getShooter();
            }
        }

        if (attacker != null && !attacker.equals(victim)) {
            lastAttackers.put(victim.getUniqueId(), new AttackerInfo(attacker.getUniqueId(), System.currentTimeMillis()));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        lastAttackers.remove(event.getPlayer().getUniqueId());
        plugin.getCooldownManager().clearAll(event.getPlayer().getUniqueId());
    }

    public Player getLastAttacker(Player victim, int windowSeconds) {
        AttackerInfo info = lastAttackers.get(victim.getUniqueId());
        if (info == null) return null;

        long elapsed = (System.currentTimeMillis() - info.timestamp) / 1000L;
        if (elapsed > windowSeconds) {
            lastAttackers.remove(victim.getUniqueId());
            return null;
        }

        Player attacker = org.bukkit.Bukkit.getPlayer(info.attackerUuid);
        if (attacker != null && attacker.isOnline()) {
            return attacker;
        }
        return null;
    }

    private static class AttackerInfo {
        private final UUID attackerUuid;
        private final long timestamp;

        public AttackerInfo(UUID attackerUuid, long timestamp) {
            this.attackerUuid = attackerUuid;
            this.timestamp = timestamp;
        }
    }
}
