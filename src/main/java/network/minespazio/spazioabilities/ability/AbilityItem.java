package network.minespazio.spazioabilities.ability;

import network.minespazio.spazioabilities.SpazioAbilities;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class AbilityItem {

    private final SpazioAbilities plugin;
    private final NamespacedKey key;

    public AbilityItem(SpazioAbilities plugin) {
        this.plugin = plugin;
        this.key = new NamespacedKey(plugin, "spazio_ability_id");
    }

    public ItemStack createItem(AbilityType type, int amount) {
        String path = "abilities." + type.getConfigKey() + ".";

        String matName = plugin.getConfig().getString(path + "material", "FEATHER");
        Material mat = Material.matchMaterial(matName);
        if (mat == null) mat = Material.FEATHER;

        ItemStack item = new ItemStack(mat, Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        String name = plugin.getConfig().getString(path + "name", "&b&l" + type.name());
        meta.setDisplayName(plugin.getConfigManager().color(name));

        List<String> rawLore = plugin.getConfig().getStringList(path + "lore");
        List<String> colorLore = new ArrayList<>();
        for (String l : rawLore) {
            colorLore.add(plugin.getConfigManager().color(l));
        }
        meta.setLore(colorLore);

        boolean glow = plugin.getConfig().getBoolean(path + "glow", true);
        if (glow) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        // Attach PersistentDataContainer tag
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, type.name());
        item.setItemMeta(meta);

        return item;
    }

    public AbilityType getAbilityType(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        String value = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if (value == null) return null;

        return AbilityType.fromString(value);
    }

    public NamespacedKey getKey() {
        return key;
    }
}
