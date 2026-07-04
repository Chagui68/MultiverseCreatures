package com.Chagui68;

import com.Chagui68.ability.FreezeAbility;
import com.Chagui68.entities.Mahoraga;
import com.Chagui68.entities.CreeperJr;
import com.Chagui68.entities.DioBoss;
import com.Chagui68.entities.HeadSlime;
import com.Chagui68.entities.MobHandler;
import com.Chagui68.entities.ZombieHorseTrap;
import com.Chagui68.items.food.HeadSlimeGelatin;
import com.Chagui68.items.misc.HeadSlimeHeart;
import com.Chagui68.items.misc.MilitaryComponent;
import com.Chagui68.items.misc.MilitaryMine;
import com.Chagui68.items.misc.StarCore;
import com.Chagui68.listener.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class MultiverseCreatures extends JavaPlugin {

    private FreezeAbility freezeAbility;
    private DioBoss dioBoss;
    private DioStandHandler dioStandHandler;
    private CreeperJr creeperJr;
    private HeadSlime headSlime;
    private ZombieHorseTrap zombieHorseTrap;
    private Mahoraga mahoraga;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        registerRecipes();

        freezeAbility = new FreezeAbility(this);
        dioBoss = new DioBoss(this);
        dioStandHandler = new DioStandHandler(this);
        creeperJr = new CreeperJr(this);
        headSlime = new HeadSlime(this);
        zombieHorseTrap = new ZombieHorseTrap(this);
        mahoraga = new Mahoraga(this);

        MobHandler mobHandler = new MobHandler(this);
        getServer().getPluginManager().registerEvents(mobHandler, this);
        getServer().getPluginManager().registerEvents(new ItemFoodHandler(this), this);
        getServer().getPluginManager().registerEvents(new EntitiesIAHandler(), this);
        getServer().getPluginManager().registerEvents(new ItemCombatHandler(this), this);
        getServer().getPluginManager().registerEvents(new IceCrownHandler(this), this);
        getServer().getPluginManager().registerEvents(new WirtsLanternHandler(this), this);
        getServer().getPluginManager().registerEvents(new MantisClawsHandler(this), this);
        getServer().getPluginManager().registerEvents(new MineHandler(this), this);
        getServer().getPluginManager().registerEvents(new RecipeGuardListener(), this);

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

        NamespacedKey mineKey = new NamespacedKey(this, "military_mine");
        ShapedRecipe mineRecipe = new ShapedRecipe(mineKey, MilitaryMine.MILITARY_MINE.clone());
        mineRecipe.shape("IMI", "MTM", "IMI");
        mineRecipe.setIngredient('I', Material.IRON_BLOCK);
        mineRecipe.setIngredient('T', Material.TNT);
        mineRecipe.setIngredient('M', new RecipeChoice.ExactChoice(MilitaryComponent.MILITARY_COMPONENT.clone()));
        Bukkit.addRecipe(mineRecipe);

        NamespacedKey gelatinKey = new NamespacedKey(this, "head_slime_gelatin");
        ShapedRecipe gelatinRecipe = new ShapedRecipe(gelatinKey, HeadSlimeGelatin.HEAD_SLIME_GELATIN.clone());
        gelatinRecipe.shape("ASA", "SHS", "ASA");
        gelatinRecipe.setIngredient('A', Material.APPLE);
        gelatinRecipe.setIngredient('S', Material.SLIME_BALL);
        gelatinRecipe.setIngredient('H', new RecipeChoice.ExactChoice(HeadSlimeHeart.HEAD_SLIME_HEART.clone()));
        Bukkit.addRecipe(gelatinRecipe);
    }

    public FreezeAbility getFreezeAbility() {
        return freezeAbility;
    }

    public DioBoss getDioBoss() {
        return dioBoss;
    }

    public DioStandHandler getDioStandHandler() {
        return dioStandHandler;
    }

    public CreeperJr getCreeperJr() {
        return creeperJr;
    }

    public ZombieHorseTrap getZombieHorseTrap() {
        return zombieHorseTrap;
    }

    public HeadSlime getHeadSlime() {
        return headSlime;
    }

    public Mahoraga getMahoraga() {
        return mahoraga;
    }
}