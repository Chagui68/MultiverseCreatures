package com.Chagui68.listener.recipes;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;

import java.util.Set;

public class RecipeGuardListener implements Listener {

    public static boolean isCustomItem(ItemStack item) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return false;
        Set<NamespacedKey> keys = item.getItemMeta().getPersistentDataContainer().getKeys();
        for (NamespacedKey key : keys) {
            if (key.getNamespace().equals("multiversecreatures")) return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        ItemStack result = event.getInventory().getResult();
        if (result != null && result.getType() != Material.AIR && isCustomItem(result)) return;

        for (ItemStack ingredient : event.getInventory().getMatrix()) {
            if (isCustomItem(ingredient)) {
                event.getInventory().setResult(null);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        if (isCustomItem(event.getSource())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        SmithingInventory inv = event.getInventory();
        if (isCustomItem(inv.getInputEquipment()) || isCustomItem(inv.getInputMineral())) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        if (isCustomItem(event.getInventory().getFirstItem()) ||
                isCustomItem(event.getInventory().getSecondItem())) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBrew(BrewEvent event) {
        if (isCustomItem(event.getContents().getIngredient())) {
            event.setCancelled(true);
        }
    }
}
