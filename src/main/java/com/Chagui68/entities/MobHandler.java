package com.Chagui68.entities;

import com.Chagui68.MultiverseCreatures;
import com.Chagui68.items.misc.IceCrown;
import com.Chagui68.items.weapons.Excalibur;
import com.Chagui68.items.food.ScoobyCookie;
import com.Chagui68.items.misc.MantisClaws;
import com.Chagui68.items.misc.StarCore;
import com.Chagui68.items.misc.WirtsLantern;
import io.papermc.paper.world.MoonPhase;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.Chagui68.items.weapons.Excalibur.EXCALIBUR_SWORD;
import static com.Chagui68.items.food.ScoobyCookie.SCOOBY_COOKIE;

public class MobHandler implements Listener {

    private static final double SHAGGY_CHANCE = 0.3;

    private final Random random = new Random();
    private final MultiverseCreatures plugin;

    public MobHandler(MultiverseCreatures plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL
                && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER_EGG
                && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.REINFORCEMENTS
                && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER) {
            return;
        }

        Location loc = event.getLocation();
        EntityType type = event.getEntityType();

        switch (type) {
            case ZOMBIE -> handleZombieSpawn(event, loc);
            case SLIME -> handleSlimeSpawn(event, loc);
            case CREEPER -> handleCreeperSpawn(event, loc);
            case WANDERING_TRADER -> handleTraderSpawn(event);
        }
    }

    private void handleZombieSpawn(CreatureSpawnEvent event, Location loc) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) return;
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) return;

        World world = loc.getWorld();

        double dioChance = plugin.getConfig().getDouble("dio-boss.spawn-chance", 0.005);
        if (random.nextDouble() < dioChance) {
            event.setCancelled(true);
            plugin.getDioBoss().trySpawnDio(loc);
            return;
        }

        if (world.getMoonPhase() == MoonPhase.FULL_MOON && random.nextDouble() < 0.001) {
            event.setCancelled(true);
            plugin.getZombieHorseTrap().trySpawn(loc);
            return;
        }

        double adapterChance = plugin.getConfig().getDouble("mahoraga.spawn-chance", 0.02);
        if (random.nextDouble() < adapterChance) {
            event.setCancelled(true);
            plugin.getMahoraga().trySpawn(loc);
        }
    }

    private void handleSlimeSpawn(CreatureSpawnEvent event, Location loc) {
        if (event.getEntity().getScoreboardTags().contains("MSC_HeadSlime")) return;

        double chance = plugin.getConfig().getDouble("head-slime.spawn-chance", 0.1);
        if (random.nextDouble() < chance) {
            event.setCancelled(true);
            plugin.getHeadSlime().trySpawn(loc);
        }
    }

    private void handleCreeperSpawn(CreatureSpawnEvent event, Location loc) {
        if (event.getEntity().getScoreboardTags().contains("MSC_CreeperJr")) return;

        double chance = plugin.getConfig().getDouble("creeper-jr.spawn-chance", 0.15);
        if (random.nextDouble() < chance) {
            event.setCancelled(true);
            plugin.getCreeperJr().trySpawn(loc);
        }
    }

    private void handleTraderSpawn(CreatureSpawnEvent event) {
        if (random.nextDouble() < SHAGGY_CHANCE) {
            equipWanderingVillager((WanderingTrader) event.getEntity());
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

        ItemStack mantisClaws = MantisClaws.MANTIS_CLAWS_ITEM.clone();
        MerchantRecipe mantisTrade = new MerchantRecipe(mantisClaws, 999);
        mantisTrade.addIngredient(new ItemStack(Material.IRON_INGOT, 16));
        mantisTrade.addIngredient(new ItemStack(Material.STRING, 8));
        trades.add(mantisTrade);

        trader.setRecipes(trades);
        trader.addScoreboardTag("MSC_MultiverseMerchant");
    }

    public void spawnShaggy(Location location) {
        WanderingTrader shaggy = (WanderingTrader) location.getWorld().spawnEntity(location, EntityType.WANDERING_TRADER);
        equipWanderingVillager(shaggy);
    }
}