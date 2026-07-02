package com.Chagui68.items.misc;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class StarCore {

    public static final NamespacedKey STAR_CORE_KEY = new NamespacedKey("multiversecreatures", "msc_star_core");
    public static final ItemStack STAR_CORE = new ItemStack(Material.NETHER_STAR);

    static {
        ItemMeta meta = STAR_CORE.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Star Core");

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "The strongest of this world, mixed with");
            lore.add(ChatColor.GRAY + "the strongest of another, forged around");
            lore.add(ChatColor.GRAY + "the heart of a superior entity.");
            lore.add("");
            lore.add(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "The heart of a fallen star,");
            lore.add(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "beating with ancient power.");
            lore.add("");
            lore.add(ChatColor.DARK_GRAY + "✦ " + ChatColor.GRAY + "Multiverse" + ChatColor.DARK_GRAY + " ✦");

            meta.setLore(lore);
            meta.getPersistentDataContainer().set(STAR_CORE_KEY, PersistentDataType.INTEGER, 1);
            STAR_CORE.setItemMeta(meta);
        }
    }
}