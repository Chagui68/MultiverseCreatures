package com.Chagui68.commands;

import com.Chagui68.items.misc.IceCrown;
import com.Chagui68.items.weapons.Excalibur;
import com.Chagui68.items.food.ScoobyCookie;
import com.Chagui68.items.misc.StarCore;
import com.Chagui68.items.misc.WirtsLantern;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.Chagui68.entities.MobHandler;
import com.Chagui68.MultiverseCreatures;

public class MSCCommand implements CommandExecutor, TabCompleter {

    private final MultiverseCreatures plugin;
    private final MobHandler mobHandler;

    public MSCCommand(MultiverseCreatures plugin, MobHandler mobHandler) {
        this.plugin = plugin;
        this.mobHandler = mobHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "spawn":
                handleSpawn(sender, args);
                break;
            case "reload":
                handleReload(sender);
                break;
            case "give":
                handleGive(sender, args);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown command. Use /msc for help.");
                sendHelp(sender);
                break;
        }

        return true;
    }

    private void handleSpawn(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can spawn entities.");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /msc spawn <shaggy>");
            return;
        }

        Player p = (Player) sender;
        String type = args[1].toLowerCase();

        if (type.equals("merchant")) {
            mobHandler.spawnShaggy(p.getLocation());
            sender.sendMessage(ChatColor.GREEN + "Spawned Multiverse Merchant!");
        } else {
            sender.sendMessage(ChatColor.RED + "Unknown entity type. Available: merchant");
        }
    }

    private void handleReload(CommandSender sender) {
        plugin.reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "[MSC] Configuration reloaded.");
    }

    private void handleGive(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can receive items.");
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /msc give <item> [amount]");
            sender.sendMessage(ChatColor.YELLOW + "Available items: scoobycookie, excalibur, icecrown, wirtslantern, starcore");
            return;
        }

        Player target = (Player) sender;
        String itemName = args[1].toLowerCase();
        int amount = 1;

        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
                if (amount < 1 || amount > 64) {
                    sender.sendMessage(ChatColor.RED + "Amount must be between 1 and 64.");
                    return;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid amount.");
                return;
            }
        }

        ItemStack item = switch (itemName) {
            case "scoobycookie", "cookie" -> ScoobyCookie.SCOOBY_COOKIE.clone();
            case "excalibur", "sword" -> Excalibur.EXCALIBUR_SWORD.clone();
            case "icecrown", "crown" -> IceCrown.ICE_CROWN.clone();
            case "wirtslantern", "lantern" -> WirtsLantern.WIRTS_LANTERN.clone();
            case "starcore", "star" -> StarCore.STAR_CORE.clone();
            default -> null;
        };

        if (item == null) {
            sender.sendMessage(ChatColor.RED + "Unknown item. Available: scoobycookie, excalibur, icecrown, wirtslantern, starcore");
            return;
        }

        item.setAmount(amount);
        target.getInventory().addItem(item);
        sender.sendMessage(ChatColor.GREEN + "Gave " + amount + "x " + item.getItemMeta().getDisplayName() + ChatColor.GREEN + "!");
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "--- MultiverseCreatures Commands ---");
        sender.sendMessage(ChatColor.YELLOW + "/msc spawn <type> " + ChatColor.WHITE + "- Spawn custom mobs (merchant)");
        sender.sendMessage(ChatColor.YELLOW + "/msc give <item> [amount] " + ChatColor.WHITE + "- Give custom items (scoobycookie, excalibur, icecrown, wirtslantern, starcore)");
        sender.sendMessage(ChatColor.YELLOW + "/msc reload " + ChatColor.WHITE + "- Reload configuration");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.isOp()) {
            return completions;
        }

        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("spawn", "reload", "give");
            completions.addAll(subCommands.stream()
                    .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList()));
        } else if (args.length == 2) {
            String subCmd = args[0].toLowerCase();
            if (subCmd.equals("spawn")) {
                List<String> entities = Arrays.asList("merchant");
                completions.addAll(entities.stream()
                        .filter(e -> e.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList()));
            } else if (subCmd.equals("give")) {
                List<String> items = Arrays.asList("scoobycookie", "excalibur", "icecrown", "wirtslantern", "starcore");
                completions.addAll(items.stream()
                        .filter(i -> i.startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList()));
            }
        }

        return completions;
    }
}