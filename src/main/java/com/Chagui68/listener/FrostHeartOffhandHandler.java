package com.Chagui68.listener;

import com.Chagui68.items.misc.offhand.FrostHeartOffhand;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FrostHeartOffhandHandler implements Listener {

    private boolean isFrostHeart(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(FrostHeartOffhand.FROST_KEY, PersistentDataType.INTEGER);
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player p)) return;
        ItemStack off = p.getInventory().getItem(EquipmentSlot.OFF_HAND);
        if (!isFrostHeart(off)) return;
        if (!(event.getDamager() instanceof LivingEntity attacker)) return;

        attacker.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, FrostHeartOffhand.CHILL_TICKS, FrostHeartOffhand.CHILL_AMPLIFIER_SLOW, false, false));
        attacker.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, FrostHeartOffhand.CHILL_TICKS, FrostHeartOffhand.CHILL_AMPLIFIER_WEAK, false, false));
        attacker.getWorld().spawnParticle(Particle.SNOWFLAKE, attacker.getLocation().add(0, 1, 0), 10, 0.3, 0.5, 0.3, 0.05);
        attacker.getWorld().playSound(attacker.getLocation(), Sound.ENTITY_PLAYER_HURT_FREEZE, 0.8f, 1.2f);
    }
}