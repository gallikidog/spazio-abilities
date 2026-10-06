package network.minespazio.spazioabilities.ability;

import network.minespazio.spazioabilities.SpazioAbilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class AbilityManager {

    private final SpazioAbilities plugin;
    private final AbilityItem abilityItem;

    public AbilityManager(SpazioAbilities plugin) {
        this.plugin = plugin;
        this.abilityItem = new AbilityItem(plugin);
    }

    public boolean useAbility(Player user, AbilityType type, Player target) {
        if (user == null || type == null) return false;

        // Check if inhibited
        if (plugin.getCooldownManager().isInhibited(user)) {
            long remaining = plugin.getCooldownManager().getRemainingInhibit(user);
            String msg = plugin.getConfigManager().getMsg("messages.ability-inhibited", "&cSus habilidades están bloqueadas.")
                    .replace("{time}", String.valueOf(remaining));
            user.sendMessage(msg);
            user.playSound(user.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
            return false;
        }

        // Check WorldGuard PvP region rule
        if (!plugin.getWorldGuardHook().canUseAbility(user, target)) {
            String denyMsg = plugin.getConfigManager().getMsg("worldguard.deny-message", "&cNo puedes usar esta habilidad en esta zona.");
            user.sendMessage(denyMsg);
            user.playSound(user.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return false;
        }

        // Check Global Cooldown
        if (plugin.getCooldownManager().isOnGlobalCooldown(user)) {
            long remaining = plugin.getCooldownManager().getRemainingGlobalCooldown(user);
            String msg = plugin.getConfigManager().getMsg("messages.global-cooldown", "&cEspera el cooldown global.")
                    .replace("{time}", String.valueOf(remaining));
            user.sendMessage(msg);
            return false;
        }

        // Check Ability Specific Cooldown
        if (plugin.getCooldownManager().isOnCooldown(user, type)) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(user, type);
            String msg = plugin.getConfigManager().getMsg("messages.cooldown-active", "&cEspera el cooldown.")
                    .replace("{time}", String.valueOf(remaining))
                    .replace("{ability}", type.name());
            user.sendMessage(msg);
            user.playSound(user.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
            return false;
        }

        boolean success = false;
        switch (type) {
            case NINJA_SHEAR:
                success = handleNinjaShear(user);
                break;
            case SWITCHER:
                success = handleSwitcher(user, target);
                break;
            case RESISTANCE:
                success = handleResistance(user);
                break;
            case STRENGTH:
                success = handleStrength(user);
                break;
            case SPEED:
                success = handleSpeed(user);
                break;
            case ROCKET:
                success = handleRocket(user);
                break;
            case WEB_TRAP:
                success = handleWebTrap(user, target);
                break;
            case ABILITY_INHIBIT:
                success = handleAbilityInhibit(user, target);
                break;
            case THORNS_SHIELD:
                success = handleThornsShield(user);
                break;
            case ANTI_REDSTONE:
                success = handleAntiRedstone(user, target);
                break;
        }

        if (success) {
            int cd = plugin.getConfig().getInt("abilities." + type.getConfigKey() + ".cooldown", 30);
            plugin.getCooldownManager().setCooldown(user, type, cd);
            consumeItem(user);
        }

        return success;
    }

    private boolean handleNinjaShear(Player user) {
        int windowSec = plugin.getConfig().getInt("abilities.NINJA_SHEAR.combat-time-window", 15);
        Player attacker = plugin.getCombatTrackerListener().getLastAttacker(user, windowSec);

        if (attacker == null || !attacker.isOnline()) {
            user.sendMessage(plugin.getConfigManager().getMsg("messages.no-recent-attacker", "&cNo hay ningún atacante reciente."));
            return false;
        }

        if (!plugin.getWorldGuardHook().canUseAbility(user, attacker)) {
            user.sendMessage(plugin.getConfigManager().getMsg("worldguard.deny-message", "&cNo puedes usar Ninja Shear contra este jugador."));
            return false;
        }

        // Teleport behind attacker
        Location loc = attacker.getLocation();
        Vector direction = loc.getDirection().normalize().multiply(-1.5);
        Location behind = loc.clone().add(direction);
        behind.setYaw(loc.getYaw());
        behind.setPitch(loc.getPitch());

        user.teleport(behind);
        user.playSound(user.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.2f);
        attacker.playSound(attacker.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.8f);

        user.sendMessage(plugin.getConfigManager().color("&aTe has teletransportado detrás de &e" + attacker.getName() + "&a!"));
        attacker.sendMessage(plugin.getConfigManager().color("&c&l[!] &e" + user.getName() + " &chas usado Ninja Shear detrás de ti!"));
        return true;
    }

    private boolean handleSwitcher(Player user, Player target) {
        if (target == null) {
            // Find target player in line of sight within range
            double maxDist = plugin.getConfig().getDouble("abilities.SWITCHER.max-distance", 12.0);
            target = getTargetPlayerInSight(user, maxDist);
        }

        if (target == null || target.equals(user)) {
            user.sendMessage(plugin.getConfigManager().getMsg("messages.target-not-found", "&cJugador no encontrado en rango."));
            return false;
        }

        if (!plugin.getWorldGuardHook().canUseAbility(user, target)) {
            user.sendMessage(plugin.getConfigManager().getMsg("worldguard.deny-message", "&cNo puedes intercambiar posiciones con este jugador."));
            return false;
        }

        Location locUser = user.getLocation().clone();
        Location locTarget = target.getLocation().clone();

        user.teleport(locTarget);
        target.teleport(locUser);

        user.playSound(user.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.5f);
        target.playSound(target.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.5f);

        user.sendMessage(plugin.getConfigManager().color("&a¡Has intercambiado posición con &e" + target.getName() + "&a!"));
        target.sendMessage(plugin.getConfigManager().color("&c&l[!] &e" + user.getName() + " &cha usado Switcher contigo!"));
        return true;
    }

    private boolean handleResistance(Player user) {
        int duration = plugin.getConfig().getInt("abilities.RESISTANCE.duration-seconds", 6);
        int amp = plugin.getConfig().getInt("abilities.RESISTANCE.amplifier", 2);

        user.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, duration * 20, amp, false, true, true));
        user.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, duration * 20, 1, false, true, true));
        user.playSound(user.getLocation(), Sound.ITEM_ARMOR_EQUIP_IRON, 1.0f, 1.0f);
        user.sendMessage(plugin.getConfigManager().color("&a¡Has activado &ePortable Resistance &a(&eResistencia " + (amp + 1) + "&a)!"));
        return true;
    }

    private boolean handleStrength(Player user) {
        int duration = plugin.getConfig().getInt("abilities.STRENGTH.duration-seconds", 5);
        int amp = plugin.getConfig().getInt("abilities.STRENGTH.amplifier", 1);

        user.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, duration * 20, amp, false, true, true));
        user.playSound(user.getLocation(), Sound.ENTITY_BLAZE_AMBIENT, 1.0f, 1.2f);
        user.sendMessage(plugin.getConfigManager().color("&c¡Has activado &ePortable Strength &c(&eFuerza " + (amp + 1) + "&c)!"));
        return true;
    }

    private boolean handleSpeed(Player user) {
        int duration = plugin.getConfig().getInt("abilities.SPEED.duration-seconds", 8);
        int amp = plugin.getConfig().getInt("abilities.SPEED.amplifier", 2);

        user.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration * 20, amp, false, true, true));
        user.playSound(user.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 1.5f);
        user.sendMessage(plugin.getConfigManager().color("&e¡Has activado &ePortable Speed &e(&eVelocidad " + (amp + 1) + "&e)!"));
        return true;
    }

    private boolean handleRocket(Player user) {
        double boost = plugin.getConfig().getDouble("abilities.ROCKET.boost-power", 1.8);
        user.setVelocity(new Vector(0, boost, 0));
        plugin.getCooldownManager().addFallImmunity(user);

        user.playSound(user.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 1.0f);
        user.sendMessage(plugin.getConfigManager().color("&6¡Has usado &eRocket Boost &6para elevarte!"));
        return true;
    }

    private boolean handleWebTrap(Player user, Player target) {
        if (target == null) {
            target = getTargetPlayerInSight(user, 6.0);
        }

        if (target == null || target.equals(user)) {
            user.sendMessage(plugin.getConfigManager().getMsg("messages.target-not-found", "&cJugador no encontrado en rango."));
            return false;
        }

        if (!plugin.getWorldGuardHook().canUseAbility(user, target)) {
            user.sendMessage(plugin.getConfigManager().getMsg("worldguard.deny-message", "&cNo puedes encerrar a este jugador en esta zona."));
            return false;
        }

        int durationSec = plugin.getConfig().getInt("abilities.WEB_TRAP.trap-duration-seconds", 5);
        Location center = target.getLocation().getBlock().getLocation();

        List<Block> placedWebs = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = 0; y <= 1; y++) {
                    Block b = center.clone().add(x, y, z).getBlock();
                    if (b.getType().isAir()) {
                        b.setType(Material.COBWEB);
                        placedWebs.add(b);
                    }
                }
            }
        }

        target.playSound(target.getLocation(), Sound.ENTITY_SPIDER_AMBIENT, 1.0f, 0.8f);
        user.sendMessage(plugin.getConfigManager().color("&f¡Has atrapado a &e" + target.getName() + " &fen telarañas por " + durationSec + "s!"));
        target.sendMessage(plugin.getConfigManager().color("&c&l[!] &e" + user.getName() + " &cte ha atrapado en telarañas!"));

        // Remove cobwebs after duration
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Block b : placedWebs) {
                if (b.getType() == Material.COBWEB) {
                    b.setType(Material.AIR);
                }
            }
        }, durationSec * 20L);

        return true;
    }

    private boolean handleAbilityInhibit(Player user, Player target) {
        if (target == null) {
            target = getTargetPlayerInSight(user, 5.0);
        }

        if (target == null || target.equals(user)) {
            user.sendMessage(plugin.getConfigManager().getMsg("messages.target-not-found", "&cJugador no encontrado."));
            return false;
        }

        if (!plugin.getWorldGuardHook().canUseAbility(user, target)) {
            user.sendMessage(plugin.getConfigManager().getMsg("worldguard.deny-message", "&cNo puedes usar este inhibidor en esta zona."));
            return false;
        }

        int durationSec = plugin.getConfig().getInt("abilities.ABILITY_INHIBIT.inhibit-duration-seconds", 10);
        plugin.getCooldownManager().setInhibited(target, durationSec);

        target.playSound(target.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 0.5f);
        user.sendMessage(plugin.getConfigManager().color("&5¡Has bloqueado las habilidades de &e" + target.getName() + " &5por " + durationSec + "s!"));
        target.sendMessage(plugin.getConfigManager().color("&c&l[!] &e" + user.getName() + " &cha bloqueado tus habilidades por &e" + durationSec + "s&c!"));
        return true;
    }

    private boolean handleThornsShield(Player user) {
        int durationSec = plugin.getConfig().getInt("abilities.THORNS_SHIELD.duration-seconds", 6);
        plugin.getCooldownManager().setThornsShield(user, durationSec);

        user.playSound(user.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1.0f, 1.2f);
        user.sendMessage(plugin.getConfigManager().color("&9¡Has activado &eThorns Shield &9por " + durationSec + "s!"));
        return true;
    }

    private boolean handleAntiRedstone(Player user, Player target) {
        if (target == null) {
            target = getTargetPlayerInSight(user, 5.0);
        }

        if (target == null || target.equals(user)) {
            user.sendMessage(plugin.getConfigManager().getMsg("messages.target-not-found", "&cJugador no encontrado."));
            return false;
        }

        if (!plugin.getWorldGuardHook().canUseAbility(user, target)) {
            user.sendMessage(plugin.getConfigManager().getMsg("worldguard.deny-message", "&cNo puedes usar Anti-Redstone en esta zona."));
            return false;
        }

        // Strip positive potion effects
        List<PotionEffectType> positiveEffects = List.of(
                PotionEffectType.SPEED, PotionEffectType.STRENGTH, PotionEffectType.RESISTANCE,
                PotionEffectType.REGENERATION, PotionEffectType.ABSORPTION, PotionEffectType.FIRE_RESISTANCE,
                PotionEffectType.HASTE, PotionEffectType.INVISIBILITY
        );

        for (PotionEffectType type : positiveEffects) {
            if (target.hasPotionEffect(type)) {
                target.removePotionEffect(type);
            }
        }

        target.playSound(target.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 0.6f);
        user.sendMessage(plugin.getConfigManager().color("&4¡Has disipado las pociones positivas de &e" + target.getName() + "&4!"));
        target.sendMessage(plugin.getConfigManager().color("&c&l[!] &e" + user.getName() + " &cha eliminado tus efectos de pociones!"));
        return true;
    }

    private Player getTargetPlayerInSight(Player user, double maxDistance) {
        List<Player> nearby = new ArrayList<>();
        for (Player p : user.getWorld().getPlayers()) {
            if (!p.equals(user) && p.getLocation().distanceSquared(user.getLocation()) <= (maxDistance * maxDistance)) {
                nearby.add(p);
            }
        }

        Vector dir = user.getEyeLocation().getDirection().normalize();
        Player closest = null;
        double minAngle = 0.5; // Cosine threshold

        for (Player p : nearby) {
            Vector toTarget = p.getEyeLocation().toVector().subtract(user.getEyeLocation().toVector()).normalize();
            double dot = dir.dot(toTarget);
            if (dot > minAngle) {
                minAngle = dot;
                closest = p;
            }
        }
        return closest;
    }

    private void consumeItem(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item != null && item.getType() != Material.AIR) {
            item.setAmount(item.getAmount() - 1);
            if (item.getAmount() <= 0) {
                player.getInventory().setItemInMainHand(null);
            }
        }
    }

    public AbilityItem getAbilityItem() {
        return abilityItem;
    }
}
