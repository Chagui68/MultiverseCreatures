package com.Chagui68;

import com.Chagui68.entities.MobHandler;
import com.Chagui68.items.misc.StarCore;
import com.Chagui68.listener.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class MultiverseCreatures extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        registerRecipes();

        MobHandler mobHandler = new MobHandler();
        getServer().getPluginManager().registerEvents(mobHandler, this);
        getServer().getPluginManager().registerEvents(new ItemFoodHandler(), this);
        getServer().getPluginManager().registerEvents(new EntitiesIAHandler(), this);
        getServer().getPluginManager().registerEvents(new ItemCombatHandler(this), this);
        getServer().getPluginManager().registerEvents(new IceCrownHandler(this), this);
        getServer().getPluginManager().registerEvents(new WirtsLanternHandler(this), this);

        com.Chagui68.commands.MSCCommand mscCommand = new com.Chagui68.commands.MSCCommand(this, mobHandler);
        getCommand("msc").setExecutor(mscCommand);
        getCommand("msc").setTabCompleter(mscCommand);
    }

    private void registerRecipes() {
        NamespacedKey key = new NamespacedKey(this, "star_core");
        ShapedRecipe recipe = new ShapedRecipe(key, StarCore.STAR_CORE.clone());

        recipe.shape("NBN", "BSB", "NBN");

        recipe.setIngredient('N', Material.NETHERITE_BLOCK);
        recipe.setIngredient('B', Material.DIAMOND_BLOCK);
        recipe.setIngredient('S', Material.NETHER_STAR);

        Bukkit.addRecipe(recipe);
    }
}