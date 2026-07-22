package com.Chagui68.items.weapons.magic;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ChaosForge {

    public static final NamespacedKey FORGE_KEY = new NamespacedKey("multiversecreatures", "msc_chaos_forge");
    public static final ItemStack CHAOS_FORGE = new ItemStack(Material.ANVIL);

    public static final int MAX_ENCHANT_LEVEL = 30;
    public static final String REFORGED_PDC_KEY = "msc_chaos_reforged";
    public static final String REFORGED_LORE_TAG = ChatColor.DARK_RED + "" + ChatColor.ITALIC + "⟡ Reforged by Chaos ⟡";

    static {
        ItemMeta meta = CHAOS_FORGE.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Chaos Forge");

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "A portable anvil laced with entropy.");
            lore.add(ChatColor.GRAY + "It cannot create — only twist what");
            lore.add(ChatColor.GRAY + "is already written upon an item.");
            lore.add("");
            lore.add(ChatColor.AQUA + "Item Ability: " + ChatColor.WHITE + "Reforge " + ChatColor.GRAY + "(Right-Click while holding target)");
            lore.add(ChatColor.GRAY + "  Hold an enchanted item in your main hand and");
            lore.add(ChatColor.GRAY + "  right-click with the Forge in your off-hand.");
            lore.add(ChatColor.GRAY + "  Each existing enchantment on the target rises");
            lore.add(ChatColor.GRAY + "  by " + ChatColor.GOLD + "+1 level " + ChatColor.GRAY + "(cap " + ChatColor.GOLD + "30" + ChatColor.GRAY + ").");
            lore.add("");
            lore.add(ChatColor.WHITE + "Restrictions:");
            lore.add(ChatColor.RED + "  ▸ " + ChatColor.GRAY + "Only items with existing enchantments");
            lore.add(ChatColor.RED + "  ▸ " + ChatColor.GRAY + "Each item can only be reforged once");
            lore.add(ChatColor.RED + "  ▸ " + ChatColor.GRAY + "Consumes " + ChatColor.LIGHT_PURPLE + "1 Chaos Orb " + ChatColor.GRAY + "from your inventory");
            lore.add("");
            lore.add(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "\"In the orb, all possibilities;");
            lore.add(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + "in the hand, only one.\"");
            lore.add("");
            lore.add(ChatColor.DARK_GRAY + "✦ " + ChatColor.GRAY + "Multiverse" + ChatColor.DARK_GRAY + " ✦");

            meta.setLore(lore);
            meta.getPersistentDataContainer().set(FORGE_KEY, PersistentDataType.INTEGER, 1);
            CHAOS_FORGE.setItemMeta(meta);
        }
    }
}
