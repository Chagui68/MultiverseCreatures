package com.Chagui68.entities;

import com.Chagui68.MultiverseCreatures;
import com.Chagui68.utils.MscEntityUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom entity representing a Void Statue.
 * Uses a WitherSkeleton as core with a block_display root + item_display passengers
 * for the visual model. Only the root display is teleported; passengers follow.
 */
public class VoidStatue implements Listener {

    private final MultiverseCreatures plugin;
    private final Map<UUID, StatueInstance> activeStatues = new ConcurrentHashMap<>();
    private final Random random = new Random();

    private static final String TAG = "MSC_VoidStatue";
    private static final NamespacedKey STATUE_KEY = new NamespacedKey("multiversecreatures", "msc_void_statue");

    private static final double MAX_HEALTH = 150.0;
    private static final double ATTACK_DAMAGE = 12.0;
    private static final double FOLLOW_RANGE = 30.0;
    private static final double MELEE_RANGE = 3.0;
    private static final long ATTACK_COOLDOWN_MS = 2000L;

    // The root display is spawned offset -0.5 on all axes (matches the /summon command).
    private static final double ROOT_OFFSET = -0.5;
    // Parts sit on top of the root (the +y in the matrices uses the root origin).
    private static final double ROOT_Y_OFFSET = 0.0;

    private static final String[] SKIN_DATA = {
            "ewogICJ0aW1lc3RhbXAiIDogMTc4NTEwNjIwNzIwNSwKICAicHJvZmlsZUlkIiA6ICIyZDFhMzI0YjRhNDE0ODJmODNjYzk3YTA2NzY5YjI2ZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJMRUFUSEVSX0xFR0dJTkdTIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2I2N2M4MDlmZGYwNzk3ZjQ2N2QyYTE1YzY1NDQ1ZTQ0ODZhMDYyN2UwYmYzYjYwNDhlMjhiYTMwYmE0YmYwMTgiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
            "ewogICJ0aW1lc3RhbXAiIDogMTc4NTEwNjIwOTY2OCwKICAicHJvZmlsZUlkIiA6ICIwMTZiOTUxNWJhZjM0YTAxOTQ3ZGY0ZmUxMGMwMGI4NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJUcmlwbGVTaWdtYXMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjhlNzJhN2MwYmEyOWU0YjdiY2IwODE5MDdmMzZiOTM4Y2NiM2VhNjljODRhOGY3MGU5NzE0OWFhZTQ3M2Y5ZCIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
            "ewogICJ0aW1lc3RhbXAiIDogMTc4NTEwNjIxMjAyNywKICAicHJvZmlsZUlkIiA6ICI3YjA5ZDg5NWQyYjc0NTU3YmM0YTkzNWYyNjU0NWNjNCIsCiAgInByb2ZpbGVOYW1lIiA6ICJBaXJwbGFuZUdvQnJyIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzlkNzFkYzA2ZWIwODQ2NmQyOGM0OTY4OTQ2NGNhNjM1OThjOTgyZGIyOGJhNWY0NjRlNjAyZDYxZmE0YmFmNTMiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
            "ewogICJ0aW1lc3RhbXAiIDogMTc4NTEwNjIxNDM4OCwKICAicHJvZmlsZUlkIiA6ICJlYjM0ODc2IDEyZDU0YjVhOGJkZmEwY2U0YWQwNDNkMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJSYXJlczUwMDAiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODc5NWI5MzY3YTA2N2E0NWE3YWJhNTE5NGI3MzBkMTBmMzc3NmZjZGNmMGI1ZDFlOWIwNDZjMDExM2VhYzg1MSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
            "ewogICJ0aW1lc3RhbXAiIDogMTc4NTEwNjIxNTYxNywKICAicHJvZmlsZUlkIiA6ICI2NGRiNmMwNTliOTk0OTM2YTY0M2QwODEwODE0ZmJkMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVTaWx2ZXJEcmVhbXMiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGFhNjg4NjFiNGZhMjE0ODhlZWFiOTE2YzliNDRlNzBiNDhhZDMyY2VmYTk5NGQwNGQ3MzQyZDM4YzM0ZDhjNyIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
            "ewogICJ0aW1lc3RhbXAiIDogMTc4NTEwNjIxNzU2OCwKICAicHJvZmlsZUlkIiA6ICIxNzRjZmRiNGEzY2I0M2I1YmZjZGU0MjRjM2JiMmM2ZSIsCiAgInByb2ZpbGVOYW1lIiA6ICJtYXJhZWwxOCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lNzkwMTFhZTE2NTgxMzU2MjVkMjJiMTBiZmEwMDM0M2RiMTBkOGRlMzhmZTZiYzdlNjNiMGVhYjVlNWM0MmNiIiwKICAgICAgIm1ldGFkYXRhIiA6IHsKICAgICAgICAibW9kZWwiIDogInNsaW0iCiAgICAgIH0KICAgIH0KICB9Cn0=",
            "ewogICJ0aW1lc3RhbXAiIDogMTc4NTEwNjIyMDQ2MCwKICAicHJvZmlsZUlkIiA6ICI2YTQ3NjRhZWEwNWY0MTE1OTc0NzFlZjNjYWU4ZTdmZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJiYWRlZW5kamVoIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzMwYmYxZTQzNThmMzIzMGNhYzI0MjMyNWFhZWM3MWJjMjM3ZWJiODAxOWI2NDcwYTVmMTVjZWU4NDAxNjdiMDYiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
            "ewogICJ0aW1lc3RhbXAiIDogMTc4NTEwNjIyMjI0MywKICAicHJvZmlsZUlkIiA6ICJkMTQ4NjFiM2UwZmM0Njk5OTFlMTcyNTllMzdiZjZhZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJyYXhpdG9jbCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84ZWE4MzY5YTIzMjZjZGIwZDIzNTZkMDAwN2NhZWY2OWY0ZThlNTViNmNlZTA4N2I4MmJiMjMxYjdmMjVlM2IiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
            "ewogICJ0aW1lc3RhbXAiIDogMTc4NTEwNjIyMzcyNSwKICAicHJvZmlsZUlkIiA6ICIwZWQ2MDFlMDhjZTM0YjRkYWUxZmI4MDljZmEwNTM5NiIsCiAgInByb2ZpbGVOYW1lIiA6ICJOZWVkTW9yZUFjY291bnRzIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2UwYmMzNjM4ZTc2ZDQwMmNkNzgxOGU0ZDRlOWMwMWQ5ZjE2Y2E5NmNkMDViNzlhMjI5NjBiNmI4OThkNzBlZTYiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
            "ewogICJ0aW1lc3RhbXAiIDogMTc4NTEwNjIzMzQ3MywKICAicHJvZmlsZUlkIiA6ICIxMTM0OTAxMTU3ZTE0Yzg0OTE1YTNjMGY3M2RmYzM0NSIsCiAgInByb2ZpbGVOYW1lIiA6ICJab2xlZWV5IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2VmMmQzNGFlYWUyOTVhNTkwNmNkYzIyOTA5ZGFhYWY3ODEyMWMwZTcwNGEwZGMyYmM0YmM0NDM5OWU2MDI2MTkiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
            "ewogICJ0aW1lc3RhbXAiIDogMTc4NTEwNjIzNDkzOCwKICAicHJvZmlsZUlkIiA6ICIzODY2ZTk1MmIxZWE0M2E4OGE3NGI1NzAxZDVjYTAwNSIsCiAgInByb2ZpbGVOYW1lIiA6ICJGYWtlTWFybG93IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzdlZDc2Y2Q0YmMyYjhkMGYyODUzNzdhZGJhNjU4ZGFiODRhY2M5NjYxODZlMmUwYTM0NDkyZTE2N2FmNjA1YyIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9",
            "ewogICJ0aW1lc3RhbXAiIDogMTc4NTEwNjIzNjk0NiwKICAicHJvZmlsZUlkIiA6ICJkODYwY2JiYWM0Njg0ODhjOWI4MTY3MGVjZjQ5OGRmMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJDb2FzdGxpbmVDcmVhIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQ1MTdlMTEwMjdkOTk2Y2Y3MTE5YmM1YWJlZjQyZDZiNzYxZjMzZjU1OTQyM2VmZjYyYzQ3OWQ0YjQ1MzBiOGIiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ=="
    };

    // Column-major transformation matrices matching the /summon command NBT "transformation:[...]".
    // Order: m00, m10, m20, m30, m01, m11, m21, m31, m02, m12, m22, m32, m03, m13, m23, m33
    private static final float[][] TRANSFORMS = {
            {0.19612583f, 0.06802233f, 0.0f, 0.3141725f, -0.01715601f, 0.7773732f, 0.00353901f, 1.5875922f, 0.00037699f, -0.01708209f, 0.24058612f, 0.5432934f, 0.0f, 0.0f, 0.0f, 1.0f},
            {0.19612583f, 0.07935939f, 0.0f, 0.2805156f, -0.01715601f, 0.9069354f, 0.00353901f, 1.2028975f, 0.00037699f, -0.01992911f, 0.24058612f, 0.5478789f, 0.0f, 0.0f, 0.0f, 1.0f},
            {0.18692199f, 0.05455465f, 0.02968254f, 0.6330812f, -0.03449924f, 0.75183664f, 0.05171060f, 2.3278010f, -0.06545232f, -0.11940252f, 0.10261217f, 0.49092838f, 0.0f, 0.0f, 0.0f, 1.0f},
            {0.12838576f, -0.01020626f, -0.00060354f, 0.49444553f, 0.06479475f, 0.36344928f, 0.00202952f, 1.7603670f, -0.00069849f, -0.00802693f, 0.15466620f, 0.54336464f, 0.0f, 0.0f, 0.0f, 1.0f},
            {-0.19985311f, -0.01684832f, -0.00065007f, 0.38522323f, -0.09380205f, 0.74227109f, -0.04563098f, 2.3109481f, 0.00619475f, -0.10294740f, -0.10822581f, 0.53427440f, 0.0f, 0.0f, 0.0f, 1.0f},
            {0.19612583f, -0.06986078f, 0.0f, 0.68384258f, 0.01714844f, 0.79803073f, -0.00683650f, 1.5878289f, 0.00072825f, 0.03389026f, 0.24047988f, 0.54774464f, 0.0f, 0.0f, 0.0f, 1.0f},
            {0.19612583f, -0.08150424f, 0.0f, 0.71840911f, 0.01714844f, 0.93103585f, -0.00683650f, 1.19275019f, 0.00072825f, 0.03953864f, 0.24047988f, 0.53870574f, 0.0f, 0.0f, 0.0f, 1.0f},
            {0.32280454f, -0.07057050f, -0.06580384f, 0.63635448f, 0.03244408f, 0.68181032f, -0.01302422f, 0.33956553f, 0.07099500f, 0.01388233f, 0.45584585f, 0.52850506f, 0.0f, 0.0f, 0.0f, 1.0f},
            {0.32280454f, -0.14114100f, -0.06580384f, 0.56578398f, 0.03244408f, 1.36362064f, -0.01302422f, 1.02137585f, 0.07099500f, 0.02776467f, 0.45584585f, 0.54238739f, 0.0f, 0.0f, 0.0f, 1.0f},
            {0.33938970f, 0.04746488f, 0.03975559f, 0.37139694f, -0.02018774f, 0.68178250f, -0.02821957f, 0.33440229f, -0.05328546f, 0.06575314f, 0.39422924f, 0.46527505f, 0.0f, 0.0f, 0.0f, 1.0f},
            {0.33938970f, 0.09492976f, 0.03975559f, 0.41886182f, -0.02018774f, 1.36356500f, -0.02821957f, 1.01618479f, -0.05328546f, 0.13150628f, 0.39422924f, 0.53102819f, 0.0f, 0.0f, 0.0f, 1.0f},
            {0.58781250f, 0.0f, 0.0f, 0.49415948f, 0.0f, 0.82265625f, 0.0f, 1.60447542f, 0.0f, 0.0f, 0.43505859f, 0.52948991f, 0.0f, 0.0f, 0.0f, 1.0f},
            {0.58781250f, 0.0f, 0.0f, 0.49414014f, 0.0f, 0.41132813f, 0.0f, 1.19302073f, 0.0f, 0.0f, 0.43505859f, 0.52922888f, 0.0f, 0.0f, 0.0f, 1.0f},
            {0.55566890f, -0.03885615f, 0.06849926f, 0.48917432f, 0.03648867f, 0.56097854f, 0.01700993f, 1.96540354f, -0.09706398f, -0.01726462f, 0.59534525f, 0.55981726f, 0.0f, 0.0f, 0.0f, 1.0f}
    };

    // Sword transformation (right-hand): translation + rotation + scale.
    private static final float[] SWORD_TRANSFORM = {
            0.8f, 0.2f, 0.0f,        // translation
            0f, 0f, 0f,              // rotation quaternion (left rotation set programmatically below)
            1f, 1f, 1f,              // scale
            0f, 0f, 0f, 1f           // right rotation (identity)
    };

    public VoidStatue(MultiverseCreatures plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        startTicker();
        reloadExisting();
    }

    private void reloadExisting() {
        for (org.bukkit.World world : Bukkit.getWorlds()) {
            for (WitherSkeleton ws : world.getEntitiesByClass(WitherSkeleton.class)) {
                if (!ws.getScoreboardTags().contains(TAG)) continue;
                StatueInstance inst = new StatueInstance(ws);
                activeStatues.put(ws.getUniqueId(), inst);
                plugin.getLogger().info("Restarted Void Statue AI for " + ws.getUniqueId());
            }
        }
    }

    public boolean trySpawn(Location location) {
        WitherSkeleton skeleton = MscEntityUtils.spawnTagged(
                location,
                EntityType.WITHER_SKELETON,
                TAG,
                null,
                entity -> {
                    WitherSkeleton sk = (WitherSkeleton) entity;
                    sk.setAI(false);
                    sk.setCollidable(false);
                    sk.setGravity(false);
                    sk.setSilent(true);
                    sk.setCanPickupItems(false);
                    sk.setInvulnerable(false);

                    MscEntityUtils.setAttribute(sk, Attribute.MAX_HEALTH, MAX_HEALTH);
                    sk.setHealth(MAX_HEALTH);
                    MscEntityUtils.setAttribute(sk, Attribute.ATTACK_DAMAGE, ATTACK_DAMAGE);

                    sk.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, MscEntityUtils.PERMANENT_DURATION, 0, false, false));
                    sk.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, MscEntityUtils.PERMANENT_DURATION, 0, false, false));
                }
        );

        if (skeleton == null) {
            return false;
        }

        StatueInstance inst = new StatueInstance(skeleton);
        activeStatues.put(skeleton.getUniqueId(), inst);
        spawnModelDisplays(skeleton, inst);
        return true;
    }

    /**
     * Spawns the root block_display (anchored at the core entity) and attaches all
     * item_display parts (player heads) plus the sword as passengers of the root.
     * Only the root needs to be teleported each tick - passengers follow automatically.
     */
    private void spawnModelDisplays(WitherSkeleton core, StatueInstance inst) {
        // Root block_display spawned at the core's feet (matches /summon ~-0.5 ~-0.5 ~-0.5).
        Location base = core.getLocation().clone();

        BlockDisplay root = (BlockDisplay) core.getWorld().spawnEntity(base, EntityType.BLOCK_DISPLAY);
        // Use a barrier block as the invisible anchor (it is not rendered to clients).
        root.setBlock(Bukkit.createBlockData(Material.BARRIER));
        root.setBrightness(new Display.Brightness(15, 15));
        root.setViewRange(100);
        root.setShadowRadius(0.0f);
        root.setShadowStrength(0.0f);
        // No interpolation -> no jitter when teleporting.
        root.setInterpolationDelay(0);
        root.setInterpolationDuration(0);
        root.addScoreboardTag(TAG + "_ROOT");
        inst.rootDisplay = root;

        // Spawn each head part and attach it as a passenger of the root.
        for (int i = 0; i < SKIN_DATA.length && i < TRANSFORMS.length; i++) {
            ItemDisplay part = spawnHeadPart(core.getWorld(), base, SKIN_DATA[i], TRANSFORMS[i]);
            part.addScoreboardTag(TAG + "_PART");
            inst.parts.add(part);
            root.addPassenger(part);
        }

        // Spawn the sword in the right hand and attach it as a passenger of the root.
        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        if (swordMeta != null) {
            swordMeta.setUnbreakable(true);
            swordMeta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Void Blade");
            sword.setItemMeta(swordMeta);
        }

        ItemDisplay swordDisplay = (ItemDisplay) core.getWorld().spawnEntity(base, EntityType.ITEM_DISPLAY);
        swordDisplay.setItemStack(sword);
        swordDisplay.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.THIRDPERSON_RIGHTHAND);
        swordDisplay.setBrightness(new Display.Brightness(15, 15));
        swordDisplay.setViewRange(100);
        swordDisplay.setShadowRadius(0.0f);
        swordDisplay.setShadowStrength(0.0f);
        swordDisplay.setInterpolationDelay(0);
        swordDisplay.setInterpolationDuration(0);
        swordDisplay.setTransformation(new Transformation(
                new Vector3f(0.8f, 0.2f, 0.0f),
                new Quaternionf().rotateX((float) Math.toRadians(-130)).rotateY((float) Math.toRadians(90)),
                new Vector3f(1, 1, 1),
                new Quaternionf()
        ));
        swordDisplay.addScoreboardTag(TAG + "_SWORD");
        inst.swordDisplay = swordDisplay;
        root.addPassenger(swordDisplay);
    }

    private ItemDisplay spawnHeadPart(World world, Location base, String texture, float[] matrix) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            try {
                String json = new String(Base64.getDecoder().decode(texture));
                com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(json).getAsJsonObject();
                String url = obj.getAsJsonObject("textures")
                        .getAsJsonObject("SKIN")
                        .get("url").getAsString();
                PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(), "Statue");
                PlayerTextures textures = profile.getTextures();
                textures.setSkin(new URL(url));
                profile.setTextures(textures);
                meta.setOwnerProfile(profile);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to set statue head texture: " + e.getMessage());
            }
            meta.setUnbreakable(true);
            head.setItemMeta(meta);
        }

        ItemDisplay display = (ItemDisplay) world.spawnEntity(base, EntityType.ITEM_DISPLAY);
        display.setItemStack(head);
        // FIXED so the head ignores the display entity's own rotation/pitch.
        display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
        display.setBrightness(new Display.Brightness(15, 15));
        display.setViewRange(100);
        display.setShadowRadius(0.0f);
        display.setShadowStrength(0.0f);
        display.setInterpolationDelay(0);
        display.setInterpolationDuration(0);

        // The matrix array is column-major (as in the /summon NBT).
        // JOML's Matrix4f constructor is row-major, so we transpose.
        org.joml.Matrix4f mat = new org.joml.Matrix4f(
                matrix[0], matrix[4], matrix[8], matrix[12],
                matrix[1], matrix[5], matrix[9], matrix[13],
                matrix[2], matrix[6], matrix[10], matrix[14],
                matrix[3], matrix[7], matrix[11], matrix[15]
        );
        display.setTransformationMatrix(mat);

        return display;
    }

    private void startTicker() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Iterator<Map.Entry<UUID, StatueInstance>> it = activeStatues.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<UUID, StatueInstance> entry = it.next();
                    StatueInstance inst = entry.getValue();
                    WitherSkeleton core = inst.core;

                    if (core.isDead() || !core.isValid()) {
                        removeDisplays(inst);
                        it.remove();
                        continue;
                    }

                    tickStatue(inst);
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void tickStatue(StatueInstance inst) {
        WitherSkeleton core = inst.core;
        long now = System.currentTimeMillis();

        Player target = findNearestPlayer(core);

        // Only move along the ground - never fly. We keep the statue anchored at its
        // current Y when no target is around so it doesn't drift upward.
        Location coreLoc = core.getLocation();

        if (target != null) {
            double dist = coreLoc.distance(target.getLocation());

            if (dist <= MELEE_RANGE) {
                if (now - inst.lastAttackTime >= ATTACK_COOLDOWN_MS) {
                    target.damage(ATTACK_DAMAGE, core);
                    target.getWorld().playSound(coreLoc, Sound.ENTITY_WITHER_SKELETON_HURT, 0.8f, 0.5f);
                    target.getWorld().spawnParticle(Particle.SWEEP_ATTACK, target.getLocation().add(0, 1, 0), 5, 0.3, 0.5, 0.3, 0.05);
                    inst.lastAttackTime = now;
                }
            } else if (dist <= FOLLOW_RANGE) {
                // Walk toward the target along the ground - keep the same Y.
                Vector dir = target.getLocation().toVector().subtract(coreLoc.toVector());
                dir.setY(0);
                if (dir.lengthSquared() > 0.0001) {
                    dir.normalize().multiply(0.3);
                }
                Location moveLoc = coreLoc.clone().add(dir.getX(), 0, dir.getZ());
                moveLoc.setYaw((float) Math.toDegrees(Math.atan2(-dir.getX(), dir.getZ())));
                moveLoc.setPitch(0f);
                core.teleport(moveLoc);
            }
        }

        // Only teleport the root display - passengers (parts + sword) follow it.
        updateRootPosition(inst, core);
    }

    private void updateRootPosition(StatueInstance inst, WitherSkeleton core) {
        if (inst.rootDisplay == null || !inst.rootDisplay.isValid()) return;

        // The root sits at the core's location, offset -0.5 on each axis like the /summon command.
        Location rootLoc = core.getLocation().clone().add(ROOT_OFFSET, ROOT_Y_OFFSET + ROOT_OFFSET, ROOT_OFFSET);
        // Keep the root upright so the model doesn't tilt with the core's pitch.
        rootLoc.setYaw(core.getLocation().getYaw());
        rootLoc.setPitch(0f);
        inst.rootDisplay.teleport(rootLoc);
    }

    private Player findNearestPlayer(WitherSkeleton core) {
        Player nearest = null;
        double nearestDist = FOLLOW_RANGE * FOLLOW_RANGE;

        for (Entity e : core.getNearbyEntities(FOLLOW_RANGE, FOLLOW_RANGE, FOLLOW_RANGE)) {
            if (e instanceof Player p && MscEntityUtils.isValidTarget(p)) {
                double d = core.getLocation().distanceSquared(p.getLocation());
                if (d < nearestDist) {
                    nearestDist = d;
                    nearest = p;
                }
            }
        }
        return nearest;
    }

    private void removeDisplays(StatueInstance inst) {
        // Removing the root automatically removes its passengers too, but we also
        // remove them explicitly to be safe against any that may have dismounted.
        for (ItemDisplay part : inst.parts) {
            if (part != null && part.isValid()) part.remove();
        }
        inst.parts.clear();
        if (inst.swordDisplay != null && inst.swordDisplay.isValid()) inst.swordDisplay.remove();
        if (inst.rootDisplay != null && inst.rootDisplay.isValid()) inst.rootDisplay.remove();
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof WitherSkeleton ws)) return;
        if (!ws.getScoreboardTags().contains(TAG)) return;

        StatueInstance inst = activeStatues.remove(ws.getUniqueId());
        if (inst != null) {
            removeDisplays(inst);
        }

        cleanupOrphanedDisplays(ws.getWorld(), ws.getLocation(), 5);

        event.getDrops().clear();
        event.setDroppedExp(50);

        ws.getWorld().playSound(ws.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0f, 0.5f);
        ws.getWorld().spawnParticle(Particle.SMOKE, ws.getLocation(), 30, 1, 1, 1, 0.1);
    }

    private void cleanupOrphanedDisplays(World world, Location center, double radius) {
        for (Entity e : world.getNearbyEntities(center, radius, radius, radius)) {
            if (e instanceof BlockDisplay || e instanceof ItemDisplay) {
                for (String tag : e.getScoreboardTags()) {
                    if (tag.startsWith(TAG)) {
                        e.remove();
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof WitherSkeleton ws)) return;
        if (!ws.getScoreboardTags().contains(TAG)) return;

        if (event.getDamager() instanceof Player p) {
            ws.setTarget(p);
        }
    }

    @EventHandler
    public void onRemove(EntityRemoveEvent event) {
        if (!(event.getEntity() instanceof WitherSkeleton ws)) return;
        if (!ws.getScoreboardTags().contains(TAG)) return;

        StatueInstance inst = activeStatues.remove(ws.getUniqueId());
        if (inst != null) {
            removeDisplays(inst);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // No cleanup needed per-player
    }

    private static class StatueInstance {
        final WitherSkeleton core;
        BlockDisplay rootDisplay;
        final List<ItemDisplay> parts = new ArrayList<>();
        ItemDisplay swordDisplay;
        long lastAttackTime = 0;

        StatueInstance(WitherSkeleton core) {
            this.core = core;
        }
    }
}
