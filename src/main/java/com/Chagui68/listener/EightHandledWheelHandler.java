package com.Chagui68.listener;

import com.Chagui68.items.armor.EightHandledWheel;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EightHandledWheelHandler implements Listener {

    private final Plugin plugin;
    private final Map<UUID, Integer> charges = new ConcurrentHashMap<>();
    private final Map<UUID, Long> globalCooldown = new ConcurrentHashMap<>();
    private final Map<UUID, Map<org.bukkit.event.entity.EntityDamageEvent.DamageCause, Long>> causeCooldowns = new ConcurrentHashMap<>();

    public EightHandledWheelHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    private boolean isWheel(ItemStack item) {
        if (item == null || item.getType() != Material.NETHERITE_HELMET) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(EightHandledWheel.WHEEL_KEY, PersistentDataType.INTEGER);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player p)) return;
        ItemStack helm = p.getInventory().getHelmet();
        if (!isWheel(helm)) return;

        UUID uuid = p.getUniqueId();
        long now = System.currentTimeMillis();

        if (globalCooldown.getOrDefault(uuid, 0L) > now) return;

        org.bukkit.event.entity.EntityDamageEvent.DamageCause cause = event.getCause();
        Map<org.bukkit.event.entity.EntityDamageEvent.DamageCause, Long> causeMap = causeCooldowns.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());
        if (causeMap.getOrDefault(cause, 0L) > now) return;

        int c = charges.getOrDefault(uuid, EightHandledWheel.MAX_CHARGES);
        if (c <= 0) return;

        charges.put(uuid, c - 1);
        causeMap.put(cause, now + EightHandledWheel.BLOCK_DURATION_TICKS * 50L);
        globalCooldown.put(uuid, now + EightHandledWheel.BLOCK_COOLDOWN_MS);

        event.setCancelled(true);
        p.sendMessage(ChatColor.GRAY + "Wheel adapts to " + cause.name() + " (" + charges.get(uuid) + " charges remain)");
        p.getWorld().playSound(p.getLocation(), org.bukkit.Sound.BLOCK_BEACON_ACTIVATE, 0.5f, 1.5f);
        p.getWorld().spawnParticle(org.bukkit.Particle.END_ROD, p.getLocation().add(0, 1, 0), 15, 0.3, 0.5, 0.3, 0.05);

        // Regen charges over time
        if (c == EightHandledWheel.MAX_CHARGES) {
            startRegen(uuid);
        }
    }

    private void startRegen(UUID uuid) {
        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                int c = charges.getOrDefault(uuid, 0);
                if (c >= EightHandledWheel.MAX_CHARGES) {
                    cancel();
                    return;
                }
                charges.put(uuid, c + 1);
            }
        }.runTaskTimer(plugin, EightHandledWheel.CHARGE_REGEN_TICKS, EightHandledWheel.CHARGE_REGEN_TICKS);
    }
}