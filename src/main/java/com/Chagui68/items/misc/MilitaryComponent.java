package com.Chagui68.items.misc;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class MilitaryComponent {

    public static final NamespacedKey MILITARY_KEY = new NamespacedKey("multiversecreatures", "msc_military_component");
    public static final ItemStack MILITARY_COMPONENT = new ItemStack(Material.GUNPOWDER);

    static {
        ItemMeta meta = MILITARY_COMPONENT.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Military Component");

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "A piece of military-grade equipment");
            lore.add(ChatColor.GRAY + "salvaged from the battlefield.");
            lore.add("");
            lore.add(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "\"Standard issue. Nothing more, nothing less.\"");
            lore.add("");
            lore.add(ChatColor.DARK_GRAY + "✦ " + ChatColor.GRAY + "Military" + ChatColor.DARK_GRAY + " ✦");

            meta.setLore(lore);
            meta.getPersistentDataContainer().set(MILITARY_KEY, PersistentDataType.INTEGER, 1);
            MILITARY_COMPONENT.setItemMeta(meta);
        }
    }
}
