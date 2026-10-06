package network.minespazio.spazioabilities.commands;

import network.minespazio.spazioabilities.SpazioAbilities;
import network.minespazio.spazioabilities.ability.AbilityType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AbilityCommand implements CommandExecutor, TabCompleter {

    private final SpazioAbilities plugin;

    public AbilityCommand(SpazioAbilities plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("me.spazioabilities.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMsg("messages.no-permission", "&cNo tienes permisos."));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();
        if (sub.equals("reload")) {
            plugin.reloadConfig();
            sender.sendMessage(plugin.getConfigManager().color("&aConfiguración de SpazioAbilities recargada exitosamente."));
            return true;
        }

        if (sub.equals("list")) {
            sender.sendMessage(plugin.getConfigManager().color("&b&lLista de Habilidades Disponibles:"));
            for (AbilityType type : AbilityType.values()) {
                sender.sendMessage(plugin.getConfigManager().color("&7 - &e" + type.name()));
            }
            return true;
        }

        if (sub.equals("gui")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getConfigManager().getMsg("messages.only-players", "&cSolo jugadores."));
                return true;
            }
            plugin.getAbilityGUI().open((Player) sender);
            return true;
        }

        if (sub.equals("give")) {
            if (args.length < 3) {
                sender.sendMessage(plugin.getConfigManager().color("&cUso: /spazioabilities give <jugador> <habilidad> [cantidad]"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null || !target.isOnline()) {
                sender.sendMessage(plugin.getConfigManager().getMsg("messages.target-not-found", "&cJugador no encontrado."));
                return true;
            }

            AbilityType type = AbilityType.fromString(args[2]);
            if (type == null) {
                sender.sendMessage(plugin.getConfigManager().color("&cHabilidad inválida. Usa /spazioabilities list para ver la lista."));
                return true;
            }

            int amount = 1;
            if (args.length >= 4) {
                try {
                    amount = Integer.parseInt(args[3]);
                } catch (NumberFormatException ignored) {}
            }

            ItemStack item = plugin.getAbilityManager().getAbilityItem().createItem(type, amount);
            target.getInventory().addItem(item);

            sender.sendMessage(plugin.getConfigManager().color("&aSe ha entregado " + amount + "x &e" + type.name() + " &aa &e" + target.getName()));
            target.sendMessage(plugin.getConfigManager().color("&aHas recibido " + amount + "x &e" + type.name()));
            return true;
        }

        sendHelp(sender);
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(plugin.getConfigManager().color("&b&lComandos de SpazioAbilities:"));
        sender.sendMessage(plugin.getConfigManager().color("&e/spazioabilities give <jugador> <habilidad> [cantidad] &7- Otorga habilidades."));
        sender.sendMessage(plugin.getConfigManager().color("&e/spazioabilities gui &7- Abre el menú visual de habilidades."));
        sender.sendMessage(plugin.getConfigManager().color("&e/spazioabilities list &7- Muestra las habilidades disponibles."));
        sender.sendMessage(plugin.getConfigManager().color("&e/spazioabilities reload &7- Recarga la configuración."));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("give");
            completions.add("gui");
            completions.add("list");
            completions.add("reload");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                completions.add(p.getName());
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            for (AbilityType type : AbilityType.values()) {
                completions.add(type.name());
            }
        }
        return completions;
    }
}
