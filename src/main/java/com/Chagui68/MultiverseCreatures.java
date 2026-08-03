package com.Chagui68;

import com.Chagui68.ability.FreezeAbility;
import com.Chagui68.entities.miniboss.Mahoraga;
import com.Chagui68.entities.boss.ArmorStandBoss;
import com.Chagui68.entities.boss.MagicSealListener;
import com.Chagui68.entities.handler.MobHandler;
import com.Chagui68.entities.BoneShield;
import com.Chagui68.entities.ChaosMage;
import com.Chagui68.entities.CreeperJr;
import com.Chagui68.entities.miniboss.DioBoss;
import com.Chagui68.entities.EnderKnight;
import com.Chagui68.entities.FlameElemental;
import com.Chagui68.entities.FrostGolem;
import com.Chagui68.entities.HeadSlime;
import com.Chagui68.entities.ObsidianGuard;
import com.Chagui68.entities.ShadowRogue;
import com.Chagui68.entities.SoulReaper;
import com.Chagui68.entities.StormCaller;
import com.Chagui68.entities.VenomWitch;
import com.Chagui68.entities.VoidCrawler;
import com.Chagui68.entities.ZombieHorseTrap;
import com.Chagui68.items.food.HeadSlimeGelatin;
import com.Chagui68.items.components.HeadSlimeHeart;
import com.Chagui68.items.components.MilitaryComponent;
import com.Chagui68.items.misc.MilitaryMine;
import com.Chagui68.items.components.StarCore;
import com.Chagui68.listener.*;
import com.Chagui68.music.MusicManager;
import com.Chagui68.ritual.BossDimensionManager;
import com.Chagui68.ritual.RitualManager;

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
    private ArmorStandBoss armorStandBoss;
    private MagicSealListener magicSealListener;
    private MusicManager musicManager;
    private BossDimensionManager bossDimensionManager;
    private RitualManager ritualManager;
    private ShadowRogue shadowRogue;
    private FlameElemental flameElemental;
    private FrostGolem frostGolem;
    private VoidCrawler voidCrawler;
    private StormCaller stormCaller;
    private BoneShield boneShield;
    private VenomWitch venomWitch;
    private ObsidianGuard obsidianGuard;
    private SoulReaper soulReaper;
    private ChaosMage chaosMage;
    private EnderKnight enderKnight;

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
        armorStandBoss = new ArmorStandBoss(this);
        magicSealListener = new MagicSealListener(this);
        musicManager = new MusicManager(this);
        shadowRogue = new ShadowRogue(this);
        flameElemental = new FlameElemental(this);
        frostGolem = new FrostGolem(this);
        voidCrawler = new VoidCrawler(this);
        stormCaller = new StormCaller(this);
        boneShield = new BoneShield(this);
        venomWitch = new VenomWitch(this);
        obsidianGuard = new ObsidianGuard(this);
        soulReaper = new SoulReaper(this);
        chaosMage = new ChaosMage(this);
        enderKnight = new EnderKnight(this);

        bossDimensionManager = new BossDimensionManager(this);

        ritualManager = new RitualManager(this);

        getServer().getScheduler().runTask(this, () -> {
            bossDimensionManager.createBossDimension();
        });

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
        getServer().getPluginManager().registerEvents(new BossDimensionCommandHandler(this), this);
        getServer().getPluginManager().registerEvents(new BossDimensionBlockHandler(this), this);
        getServer().getPluginManager().registerEvents(new RitualCandleListener(this), this);
        getServer().getPluginManager().registerEvents(new BossInvocationManager(this), this);

        getServer().getPluginManager().registerEvents(new com.Chagui68.listener.CinderGreatswordHandler(this), this);
        getServer().getPluginManager().registerEvents(new com.Chagui68.listener.VeilwalkerMantleHandler(this), this);
        getServer().getPluginManager().registerEvents(new com.Chagui68.listener.SoulreapScytheHandler(this), this);
        getServer().getPluginManager().registerEvents(new com.Chagui68.listener.MarrowAegisHandler(this), this);
        getServer().getPluginManager().registerEvents(new com.Chagui68.listener.ObsidianBastionHandler(this), this);
        getServer().getPluginManager().registerEvents(new com.Chagui68.listener.FrostHeartOffhandHandler(), this);
        getServer().getPluginManager().registerEvents(new com.Chagui68.listener.SkyfireTalismanHandler(this), this);
        getServer().getPluginManager().registerEvents(new com.Chagui68.listener.NullshearEdgeHandler(this), this);
        getServer().getPluginManager().registerEvents(new com.Chagui68.listener.EightHandledWheelHandler(this), this);
        getServer().getPluginManager().registerEvents(new com.Chagui68.listener.AetherPullshotHandler(this), this);
        getServer().getPluginManager().registerEvents(new com.Chagui68.listener.ChaosForgeHandler(), this);

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

    @Override
    public void onDisable() {
        if (musicManager != null) {
            musicManager.stopAll();
        }
        if (ritualManager != null) {
            ritualManager.stopAllRituals();
        }

        if (bossDimensionManager != null) {
            bossDimensionManager.unloadBossDimension();
        }
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

    public ArmorStandBoss getArmorStandBoss() {
        return armorStandBoss;
    }

    public MagicSealListener getMagicSealListener() {
        return magicSealListener;
    }

    public BossDimensionManager getBossDimensionManager() {
        return bossDimensionManager;
    }

    public RitualManager getRitualManager() {
        return ritualManager;
    }

    public MusicManager getMusicManager() {
        return musicManager;
    }

    public ShadowRogue getShadowRogue() {
        return shadowRogue;
    }

    public FlameElemental getFlameElemental() {
        return flameElemental;
    }

    public FrostGolem getFrostGolem() {
        return frostGolem;
    }

    public VoidCrawler getVoidCrawler() {
        return voidCrawler;
    }

    public StormCaller getStormCaller() {
        return stormCaller;
    }

    public BoneShield getBoneShield() {
        return boneShield;
    }

    public VenomWitch getVenomWitch() {
        return venomWitch;
    }

    public ObsidianGuard getObsidianGuard() {
        return obsidianGuard;
    }

    public SoulReaper getSoulReaper() {
        return soulReaper;
    }

    public ChaosMage getChaosMage() {
        return chaosMage;
    }

    public EnderKnight getEnderKnight() {
        return enderKnight;
    }
}