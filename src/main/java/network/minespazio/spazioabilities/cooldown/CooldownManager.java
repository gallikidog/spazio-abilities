package network.minespazio.spazioabilities.cooldown;

import network.minespazio.spazioabilities.SpazioAbilities;
import network.minespazio.spazioabilities.ability.AbilityType;
import org.bukkit.entity.Player;

import java.util.*;

public class CooldownManager {

    private final SpazioAbilities plugin;
    private final Map<UUID, Map<AbilityType, Long>> abilityCooldowns = new HashMap<>();
    private final Map<UUID, Long> globalCooldowns = new HashMap<>();
    private final Map<UUID, Long> inhibitedPlayers = new HashMap<>();
    private final Map<UUID, Long> thornsShields = new HashMap<>();
    private final Set<UUID> fallImmunity = new HashSet<>();

    public CooldownManager(SpazioAbilities plugin) {
        this.plugin = plugin;
    }

    public boolean isOnCooldown(Player player, AbilityType type) {
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        Map<AbilityType, Long> map = abilityCooldowns.get(uuid);
        if (map != null && map.containsKey(type)) {
            if (now < map.get(type)) {
                return true;
            }
        }
        return false;
    }

    public long getRemainingCooldown(Player player, AbilityType type) {
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        Map<AbilityType, Long> map = abilityCooldowns.get(uuid);
        if (map != null && map.containsKey(type)) {
            long expire = map.get(type);
            if (now < expire) {
                return (expire - now) / 1000L + 1L;
            }
        }
        return 0L;
    }

    public void setCooldown(Player player, AbilityType type, int seconds) {
        UUID uuid = player.getUniqueId();
        long expireTime = System.currentTimeMillis() + (seconds * 1000L);

        abilityCooldowns.computeIfAbsent(uuid, k -> new HashMap<>()).put(type, expireTime);

        // Also set global cooldown
        int globalSec = plugin.getConfig().getInt("global-cooldown-seconds", 3);
        if (globalSec > 0) {
            setGlobalCooldown(player, globalSec);
        }
    }

    public boolean isOnGlobalCooldown(Player player) {
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        return globalCooldowns.containsKey(uuid) && now < globalCooldowns.get(uuid);
    }

    public long getRemainingGlobalCooldown(Player player) {
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        if (globalCooldowns.containsKey(uuid)) {
            long expire = globalCooldowns.get(uuid);
            if (now < expire) {
                return (expire - now) / 1000L + 1L;
            }
        }
        return 0L;
    }

    public void setGlobalCooldown(Player player, int seconds) {
        globalCooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (seconds * 1000L));
    }

    public boolean isInhibited(Player player) {
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        return inhibitedPlayers.containsKey(uuid) && now < inhibitedPlayers.get(uuid);
    }

    public long getRemainingInhibit(Player player) {
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        if (inhibitedPlayers.containsKey(uuid)) {
            long expire = inhibitedPlayers.get(uuid);
            if (now < expire) {
                return (expire - now) / 1000L + 1L;
            }
        }
        return 0L;
    }

    public void setInhibited(Player player, int seconds) {
        inhibitedPlayers.put(player.getUniqueId(), System.currentTimeMillis() + (seconds * 1000L));
    }

    public boolean hasThornsShield(Player player) {
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        return thornsShields.containsKey(uuid) && now < thornsShields.get(uuid);
    }

    public void setThornsShield(Player player, int seconds) {
        thornsShields.put(player.getUniqueId(), System.currentTimeMillis() + (seconds * 1000L));
    }

    public void addFallImmunity(Player player) {
        fallImmunity.add(player.getUniqueId());
    }

    public boolean removeFallImmunity(Player player) {
        return fallImmunity.remove(player.getUniqueId());
    }

    public void clearAll(UUID uuid) {
        abilityCooldowns.remove(uuid);
        globalCooldowns.remove(uuid);
        inhibitedPlayers.remove(uuid);
        thornsShields.remove(uuid);
        fallImmunity.remove(uuid);
    }
}
