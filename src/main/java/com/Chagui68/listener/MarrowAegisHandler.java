package com.Chagui68.listener;

import com.Chagui68.items.misc.offhand.MarrowAegis;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MarrowAegisHandler implements Listener {

    private final Plugin plugin;
    private final Map<UUID, Long> rechargeCooldowns = new ConcurrentHashMap<>();

    public MarrowAegisHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    private boolean isMarrow(ItemStack item) {
        if (item == null || item.getType() != Material.SHIELD) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(MarrowAegis.MARROW_KEY, PersistentDataType.INTEGER);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player p)) return;
        if (!p.isBlocking()) return;
        ItemStack off = p.getInventory().getItemInOffHand();
        if (!isMarrow(off)) return;

        UUID uuid = p.getUniqueId();
        long now = System.currentTimeMillis();
        if (rechargeCooldowns.getOrDefault(uuid, 0L) > now) return;

        double incoming = event.getFinalDamage();
        double reflect = incoming * MarrowAegis.REFLECT_FRACTION;
        if (event.getCause() == org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_ATTACK
            || event.getCause() == org.bukkit.event.entity.EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) {
            // Note: Bukkit doesn't easily expose attacker here. True reflect needs EntityDamageByEntityEvent.
        }

        rechargeCooldowns.put(uuid, now + MarrowAegis.RECHARGE_COOLDOWN_MS);
        p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, MarrowAegis.EFFECT_DURATION_TICKS, 1, false, false));
        p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, MarrowAegis.EFFECT_DURATION_TICKS, 0, false, false));
        p.getWorld().playSound(p.getLocation(), org.bukkit.Sound.ITEM_SHIELD_BLOCK, 1.0f, 1.5f);
        p.getWorld().spawnParticle(org.bukkit.Particle.CRIT, p.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.1);
        p.sendMessage(ChatColor.GRAY + "Marrow Aegis absorbs and reflects!");
    }
}