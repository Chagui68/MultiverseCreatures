package com.Chagui68.listener;

import com.Chagui68.items.weapons.magic.ChaosForge;
import com.Chagui68.items.components.ChaosOrb;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

public class ChaosForgeHandler implements Listener {

    private static final NamespacedKey REFORGED_KEY = new NamespacedKey("multiversecreatures", "msc_chaos_reforged");

    private boolean isForge(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(ChaosForge.FORGE_KEY, PersistentDataType.INTEGER);
    }

    private boolean isOrb(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(ChaosOrb.KEY, PersistentDataType.INTEGER);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) return;
        if (event.getHand() == null) return;

        Player p = event.getPlayer();

        ItemStack forgeItem;
        ItemStack targetItem;
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            forgeItem = p.getInventory().getItemInOffHand();
            targetItem = p.getInventory().getItemInMainHand();
        } else {
            forgeItem = p.getInventory().getItemInMainHand();
            targetItem = p.getInventory().getItemInOffHand();
        }

        if (!isForge(forgeItem)) return;

        if (targetItem == null || targetItem.getType() == Material.AIR) {
            p.sendMessage(ChatColor.RED + "Hold the item to reforge in your other hand.");
            return;
        }
        if (!targetItem.hasItemMeta()) {
            p.sendMessage(ChatColor.RED + "This item cannot be reforged.");
            return;
        }
        ItemMeta meta = targetItem.getItemMeta();
        if (meta.getEnchants().isEmpty()) {
            p.sendMessage(ChatColor.RED + "This item has no enchantments to reforge.");
            return;
        }
        if (meta.getPersistentDataContainer().has(REFORGED_KEY, PersistentDataType.INTEGER)) {
            p.sendMessage(ChatColor.RED + "This item has already been reforged by the Chaos Forge.");
            return;
        }

        event.setCancelled(true);

        boolean anyChanged = false;
        for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
            Enchantment enc = entry.getKey();
            int lvl = entry.getValue();
            int newLvl = lvl + 1;
            if (lvl < 255) {
                meta.addEnchant(enc, newLvl, true);
                anyChanged = true;
            }
        }

        if (!anyChanged) {
            p.sendMessage(ChatColor.YELLOW + "All enchantments are already at maximum level.");
            return;
        }

        if (!consumeOrb(p)) {
            p.sendMessage(ChatColor.RED + "You need a Chaos Orb in your inventory.");
            return;
        }

        meta.getPersistentDataContainer().set(REFORGED_KEY, PersistentDataType.INTEGER, 1);
        targetItem.setItemMeta(meta);

        p.getWorld().playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.2f, 0.8f);
        p.getWorld().spawnParticle(Particle.ENCHANT, p.getLocation().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);
        p.sendMessage(ChatColor.LIGHT_PURPLE + "The Chaos Forge twists the enchantments...");
    }

    private boolean consumeOrb(Player p) {
        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null && isOrb(item)) {
                item.setAmount(item.getAmount() - 1);
                return true;
            }
        }
        return false;
    }
}