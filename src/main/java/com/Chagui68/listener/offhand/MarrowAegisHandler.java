package com.Chagui68.listener.offhand;

import com.Chagui68.items.misc.offhand.MarrowAegis;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MarrowAegisHandler implements Listener {

    private final Map<UUID, Long> effectCooldowns = new ConcurrentHashMap<>();
    // Guard against infinite recursion when reflected damage triggers another event.
    private final Set<UUID> reflectingLock = ConcurrentHashMap.newKeySet();

    public MarrowAegisHandler(Plugin plugin) {
    }

    private boolean isMarrow(ItemStack item) {
        if (item == null || item.getType() != Material.SHIELD) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(MarrowAegis.MARROW_KEY, PersistentDataType.INTEGER);
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player p)) return;
        // Skip events we generated while reflecting damage (prevents infinite loops).
        if (reflectingLock.contains(p.getUniqueId())) return;
        if (!p.isBlocking()) return;
        ItemStack off = p.getInventory().getItemInOffHand();
        if (!isMarrow(off)) return;

        // Use the raw incoming damage, not the post-shield final damage, so that
        // blocking fully still reflects a meaningful amount.
        double incoming = event.getDamage();
        double reflect = incoming * MarrowAegis.REFLECT_FRACTION;
        if (reflect <= 0) return;

        // Resolve the actual attacker: unwrap projectiles to their shooter.
        LivingEntity attacker = resolveAttacker(event.getDamager(), p);
        if (attacker != null && attacker != p) {
            reflectingLock.add(p.getUniqueId());
            try {
                attacker.damage(reflect, p);
            } finally {
                reflectingLock.remove(p.getUniqueId());
            }
        }

        // Active effects (15s cooldown)
        UUID uuid = p.getUniqueId();
        long now = System.currentTimeMillis();
        if (effectCooldowns.getOrDefault(uuid, 0L) > now) return;
        effectCooldowns.put(uuid, now + MarrowAegis.RECHARGE_COOLDOWN_MS);

        p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, MarrowAegis.EFFECT_DURATION_TICKS, 1, false, false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, MarrowAegis.EFFECT_DURATION_TICKS, 0, false, false));
        p.getWorld().playSound(p.getLocation(), org.bukkit.Sound.ITEM_SHIELD_BLOCK, 1.0f, 1.5f);
        p.getWorld().spawnParticle(org.bukkit.Particle.CRIT, p.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.1);
        p.sendMessage(ChatColor.GRAY + "Marrow Aegis empowers!");
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) return;
        if (!(event.getEntity() instanceof Player p)) return;
        if (reflectingLock.contains(p.getUniqueId())) return;
        if (!p.isBlocking()) return;
        ItemStack off = p.getInventory().getItemInOffHand();
        if (!isMarrow(off)) return;

        // No attacker to reflect to, just apply effects with cooldown
        UUID uuid = p.getUniqueId();
        long now = System.currentTimeMillis();
        if (effectCooldowns.getOrDefault(uuid, 0L) > now) return;
        effectCooldowns.put(uuid, now + MarrowAegis.RECHARGE_COOLDOWN_MS);

        p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, MarrowAegis.EFFECT_DURATION_TICKS, 1, false, false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, MarrowAegis.EFFECT_DURATION_TICKS, 0, false, false));
        p.getWorld().playSound(p.getLocation(), org.bukkit.Sound.ITEM_SHIELD_BLOCK, 1.0f, 1.5f);
        p.getWorld().spawnParticle(org.bukkit.Particle.CRIT, p.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.1);
        p.sendMessage(ChatColor.GRAY + "Marrow Aegis empowers!");
    }

    private LivingEntity resolveAttacker(Entity damager, Player defender) {
        if (damager == null || damager == defender) return null;
        if (damager instanceof LivingEntity le) return le;
        if (damager instanceof Projectile proj) {
            ProjectileSource source = proj.getShooter();
            if (source instanceof LivingEntity le && le != defender) return le;
        }
        return null;
    }
}
