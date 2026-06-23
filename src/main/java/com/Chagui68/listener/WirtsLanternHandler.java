package com.Chagui68.listener;

import com.Chagui68.items.misc.WirtsLantern;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WirtsLanternHandler implements Listener {

    private final Plugin plugin;
    private final Random random = new Random();

    private final Set<UUID> activeRepel = ConcurrentHashMap.newKeySet();
    private static final double REPEL_RADIUS = 12.0;
    private static final int REPEL_INTERVAL_TICKS = 20;

    public WirtsLanternHandler(Plugin plugin) {
        this.plugin = plugin;
        startRepelTask();
    }

    private void startRepelTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : new java.util.HashSet<>(activeRepel)) {
                    Player p = Bukkit.getPlayer(uuid);
                    if (p == null || !p.isOnline() || !hasLantern(p)) {
                        activeRepel.remove(uuid);
                        continue;
                    }
                    repelNearbyMobs(p);
                }
            }
        }.runTaskTimer(plugin, 0L, REPEL_INTERVAL_TICKS);
    }

    private boolean hasLantern(Player p) {
        return isLantern(p.getInventory().getItemInMainHand()) || isLantern(p.getInventory().getItemInOffHand());
    }

    private boolean isLantern(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(WirtsLantern.WIRTS_LANTERN_KEY, PersistentDataType.INTEGER);
    }

    private void repelNearbyMobs(Player p) {
        Location center = p.getLocation();
        for (Entity entity : p.getWorld().getNearbyEntities(center, REPEL_RADIUS, REPEL_RADIUS, REPEL_RADIUS)) {
            if (!(entity instanceof Monster monster)) continue;
            if (monster.getTarget() == p) {
                monster.setTarget(null);
            }
            Vector direction = monster.getLocation().toVector().subtract(center.toVector());
            if (direction.lengthSquared() > 0) {
                Vector away = direction.normalize().multiply(0.6);
                away.setY(0.2);
                monster.setVelocity(monster.getVelocity().add(away));
            }
            
            if (random.nextDouble() < 0.3) {
                p.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, monster.getLocation().add(0, 1, 0), 3, 0.3, 0.3, 0.3, 0.01);
            }
        }
    }

    @EventHandler
    public void onTarget(EntityTargetEvent e) {
        if (!(e.getTarget() instanceof Player p)) return;
        if (!hasLantern(p)) return;
        if (e.getEntity() instanceof Monster) {
            if (e.getTarget().getLocation().distance(e.getEntity().getLocation()) <= REPEL_RADIUS) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemHeld(PlayerItemHeldEvent e) {
        checkLantern(e.getPlayer());
    }

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent e) {
        checkLantern(e.getPlayer());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        checkLantern(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        activeRepel.remove(e.getPlayer().getUniqueId());
    }

    private void checkLantern(Player p) {
        if (hasLantern(p)) {
            activeRepel.add(p.getUniqueId());
            p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 100, 0, false, false));
        } else {
            activeRepel.remove(p.getUniqueId());
            p.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }
    }
}