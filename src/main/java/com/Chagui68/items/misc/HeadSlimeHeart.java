package com.Chagui68.items.misc;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class HeadSlimeHeart {

    public static final NamespacedKey HEART_KEY = new NamespacedKey("multiversecreatures", "msc_head_slime_heart");
    public static final ItemStack HEAD_SLIME_HEART = new ItemStack(Material.SLIME_BALL);

    static {
        ItemMeta meta = HEAD_SLIME_HEART.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Head Slime Heart");

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "The pulsating core of a Head Slime.");
            lore.add("");
            lore.add(ChatColor.WHITE + "Crafting Ingredient");
            lore.add("");
            lore.add(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "\"It still squirms...\"");
            lore.add("");
            lore.add(ChatColor.DARK_GRAY + "✦ " + ChatColor.GRAY + "Slime Kingdom" + ChatColor.DARK_GRAY + " ✦");

            meta.setLore(lore);
            meta.getPersistentDataContainer().set(HEART_KEY, PersistentDataType.INTEGER, 1);
            HEAD_SLIME_HEART.setItemMeta(meta);
        }
    }
}
