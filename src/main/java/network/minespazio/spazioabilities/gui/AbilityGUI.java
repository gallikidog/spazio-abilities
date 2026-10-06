package network.minespazio.spazioabilities.gui;

import network.minespazio.spazioabilities.SpazioAbilities;
import network.minespazio.spazioabilities.ability.AbilityType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AbilityGUI implements Listener {

    private final SpazioAbilities plugin;
    private final String title;

    public AbilityGUI(SpazioAbilities plugin) {
        this.plugin = plugin;
        this.title = plugin.getConfigManager().color("&8Panel de Habilidades HCF");
    }

    public void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 18, title);

        int slot = 0;
        for (AbilityType type : AbilityType.values()) {
            if (slot >= 18) break;
            ItemStack item = plugin.getAbilityManager().getAbilityItem().createItem(type, 1);
            inv.setItem(slot, item);
            slot++;
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().equals(title)) {
            event.setCancelled(true);

            ItemStack clicked = event.getCurrentItem();
            if (clicked == null) return;

            AbilityType type = plugin.getAbilityManager().getAbilityItem().getAbilityType(clicked);
            if (type != null) {
                ItemStack giveItem = plugin.getAbilityManager().getAbilityItem().createItem(type, 1);
                player.getInventory().addItem(giveItem);
                player.sendMessage(plugin.getConfigManager().color("&aHas recibido 1x &e" + type.name()));
            }
        }
    }
}
