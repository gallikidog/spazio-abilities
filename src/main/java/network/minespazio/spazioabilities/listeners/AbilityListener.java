package network.minespazio.spazioabilities.listeners;

import network.minespazio.spazioabilities.SpazioAbilities;
import network.minespazio.spazioabilities.ability.AbilityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class AbilityListener implements Listener {

    private final SpazioAbilities plugin;

    public AbilityListener(SpazioAbilities plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null) return;

        AbilityType type = plugin.getAbilityManager().getAbilityItem().getAbilityType(item);
        if (type == null) return;

        event.setCancelled(true);
        plugin.getAbilityManager().useAbility(player, type, null);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Attacker hitting victim with melee attack using ability item
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();

            ItemStack mainHand = attacker.getInventory().getItemInMainHand();
            AbilityType type = plugin.getAbilityManager().getAbilityItem().getAbilityType(mainHand);

            if (type != null) {
                // If it's a melee hit ability (WEB_TRAP, ABILITY_INHIBIT, ANTI_REDSTONE, SWITCHER)
                if (type == AbilityType.WEB_TRAP || type == AbilityType.ABILITY_INHIBIT || type == AbilityType.ANTI_REDSTONE || type == AbilityType.SWITCHER) {
                    event.setCancelled(true);
                    plugin.getAbilityManager().useAbility(attacker, type, victim);
                    return;
                }
            }

            // Thorns Shield damage reflection logic
            if (plugin.getCooldownManager().hasThornsShield(victim)) {
                double originalDamage = event.getFinalDamage();
                double reflectRatio = plugin.getConfig().getDouble("abilities.THORNS_SHIELD.reflect-ratio", 0.50);
                double reflectedDamage = originalDamage * reflectRatio;

                if (reflectedDamage > 0.5) {
                    attacker.damage(reflectedDamage, victim);
                    attacker.sendMessage(plugin.getConfigManager().color("&c&l[!] &e" + victim.getName() + " &cte ha reflejado " + String.format("%.1f", reflectedDamage) + " de daño con Thorns Shield!"));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        if (plugin.getCooldownManager().removeFallImmunity(player)) {
            event.setCancelled(true);
        }
    }
}
