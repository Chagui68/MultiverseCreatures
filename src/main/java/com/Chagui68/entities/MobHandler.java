package com.Chagui68.entities;

import com.Chagui68.items.misc.IceCrown;
import com.Chagui68.items.weapons.Excalibur;
import com.Chagui68.items.food.ScoobyCookie;
import com.Chagui68.items.misc.StarCore;
import com.Chagui68.items.misc.WirtsLantern;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.Chagui68.items.weapons.Excalibur.EXCALIBUR_SWORD;
import static com.Chagui68.items.food.ScoobyCookie.SCOOBY_COOKIE;

public class MobHandler implements Listener {

    private static final double SHAGGY_CHANCE = 0.3;

    private final Random random = new Random();
  
    public MobHandler() {
    }

    @EventHandler
    public void OnSpawn(CreatureSpawnEvent entity) {
        if (entity.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL
                && entity.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG
                && entity.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CHUNK_GEN
                && entity.getSpawnReason() != CreatureSpawnEvent.SpawnReason.REINFORCEMENTS
                && entity.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER) {
            return;
        }

        double roll = random.nextDouble();
        if (entity.getEntityType() == EntityType.WANDERING_TRADER && roll < SHAGGY_CHANCE) {
            WanderingTrader wanderingTrader = (WanderingTrader) entity.getEntity();
            equipWanderingVillager(wanderingTrader);
        }
    }

    public void equipWanderingVillager(WanderingTrader trader) {
        List<MerchantRecipe> trades = new ArrayList<>();
        trader.setCustomName(ChatColor.GOLD + "Multiverse Merchant");
        trader.setCustomNameVisible(true);

        ItemStack cookies = SCOOBY_COOKIE.clone();
        cookies.setAmount(5);
        MerchantRecipe cookiesTrade = new MerchantRecipe(cookies, 999);
        cookiesTrade.addIngredient(new ItemStack(Material.DIAMOND, 20));
        trades.add(cookiesTrade);

        ItemStack excalibur = EXCALIBUR_SWORD.clone();
        MerchantRecipe excaliburTrade = new MerchantRecipe(excalibur, 1);
        ItemStack starCoreIngredient = StarCore.STAR_CORE.clone();
        starCoreIngredient.setAmount(16);
        excaliburTrade.addIngredient(starCoreIngredient);
        excaliburTrade.addIngredient(new ItemStack(Material.NETHERITE_INGOT, 32));
        trades.add(excaliburTrade);

        ItemStack iceCrown = IceCrown.ICE_CROWN.clone();
        MerchantRecipe iceCrownTrade = new MerchantRecipe(iceCrown, 1);
        iceCrownTrade.addIngredient(new ItemStack(Material.NETHER_STAR, 48));
        iceCrownTrade.addIngredient(new ItemStack(Material.BLUE_ICE, 64));
        trades.add(iceCrownTrade);

        ItemStack wirtsLantern = WirtsLantern.WIRTS_LANTERN.clone();
        MerchantRecipe lanternTrade = new MerchantRecipe(wirtsLantern, 1);
        lanternTrade.addIngredient(new ItemStack(Material.SOUL_SAND, 32));
        lanternTrade.addIngredient(new ItemStack(Material.SOUL_SOIL, 16));
        trades.add(lanternTrade);

        trader.setRecipes(trades);
        trader.addScoreboardTag("MSC_MultiverseMerchant");
    }

    public void spawnShaggy(org.bukkit.Location location) {
        WanderingTrader shaggy = (WanderingTrader) location.getWorld().spawnEntity(location,
                EntityType.WANDERING_TRADER);
        equipWanderingVillager(shaggy);
    }
}