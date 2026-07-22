package com.Chagui68.listener;

import com.Chagui68.items.armor.ObsidianBastion;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ObsidianBastionHandler implements Listener {

    private static final NamespacedKey HP_MOD_KEY =
        new NamespacedKey("multiversecreatures", "obsidian_bastion_health_modifier");
    private static final NamespacedKey SPEED_MOD_KEY =
        new NamespacedKey("multiversecreatures", "obsidian_bastion_speed_modifier");

    private final Set<UUID> hasSetBonus = ConcurrentHashMap.newKeySet();

    private boolean isBastionPiece(ItemStack item, NamespacedKey key) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.INTEGER);
    }

    private boolean isFullSet(Player p) {
        return isBastionPiece(p.getInventory().getHelmet(), ObsidianBastion.HELMET_KEY)
            && isBastionPiece(p.getInventory().getChestplate(), ObsidianBastion.CHEST_KEY)
            && isBastionPiece(p.getInventory().getLeggings(), ObsidianBastion.LEGS_KEY)
            && isBastionPiece(p.getInventory().getBoots(), ObsidianBastion.BOOTS_KEY);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (isFullSet(event.getPlayer())) {
            UUID id = event.getPlayer().getUniqueId();
            hasSetBonus.add(id);
            applySetBonus(event.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (hasSetBonus.remove(event.getPlayer().getUniqueId())) {
            removeSetBonus(event.getPlayer());
        }
    }

    @EventHandler
    public void onItemBreak(PlayerItemBreakEvent event) {
        if (event.getBrokenItem().getType() == Material.NETHERITE_HELMET
            || event.getBrokenItem().getType() == Material.NETHERITE_CHESTPLATE
            || event.getBrokenItem().getType() == Material.NETHERITE_LEGGINGS
            || event.getBrokenItem().getType() == Material.NETHERITE_BOOTS) {
            org.bukkit.scheduler.BukkitRunnable task = new org.bukkit.scheduler.BukkitRunnable() {
                @Override
                public void run() {
                    checkSetBonus(event.getPlayer());
                }
            };
            task.runTaskLater(org.bukkit.Bukkit.getPluginManager().getPlugin("MultiverseCreatures"), 1L);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player p)) return;
        if (!hasSetBonus.contains(p.getUniqueId())) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.FIRE
            || event.getCause() == EntityDamageEvent.DamageCause.LAVA
            || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
            event.setCancelled(true);
        }
    }

    private void checkSetBonus(Player p) {
        boolean nowHas = isFullSet(p);
        UUID id = p.getUniqueId();
        boolean had = hasSetBonus.contains(id);

        if (nowHas && !had) {
            hasSetBonus.add(id);
            applySetBonus(p);
            p.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "Obsidian Bastion set bonus activated!");
        } else if (!nowHas && had) {
            hasSetBonus.remove(id);
            removeSetBonus(p);
            p.sendMessage(ChatColor.GRAY + "Obsidian Bastion set bonus lost.");
        }
    }

    private void applySetBonus(Player p) {
        AttributeInstance health = p.getAttribute(Attribute.MAX_HEALTH);
        if (health != null && health.getModifier(HP_MOD_KEY) == null) {
            AttributeModifier mod = new AttributeModifier(HP_MOD_KEY,
                ObsidianBastion.MAX_HEALTH_BONUS, AttributeModifier.Operation.ADD_NUMBER);
            double currentMax = health.getValue();
            health.addModifier(mod);
            p.setHealth(Math.min(p.getHealth() * (currentMax / health.getValue()), health.getValue()));
        }

        AttributeInstance kb = p.getAttribute(Attribute.KNOCKBACK_RESISTANCE);
        if (kb != null) {
            kb.setBaseValue(1.0);
        }

        AttributeInstance speed = p.getAttribute(Attribute.MOVEMENT_SPEED);
        if (speed != null && speed.getModifier(SPEED_MOD_KEY) == null) {
            AttributeModifier mod = new AttributeModifier(SPEED_MOD_KEY,
                -ObsidianBastion.SPEED_PENALTY, AttributeModifier.Operation.ADD_NUMBER);
            speed.addModifier(mod);
        }
    }

    private void removeSetBonus(Player p) {
        AttributeInstance health = p.getAttribute(Attribute.MAX_HEALTH);
        if (health != null && health.getModifier(HP_MOD_KEY) != null) {
            health.removeModifier(HP_MOD_KEY);
        }

        AttributeInstance speed = p.getAttribute(Attribute.MOVEMENT_SPEED);
        if (speed != null && speed.getModifier(SPEED_MOD_KEY) != null) {
            speed.removeModifier(SPEED_MOD_KEY);
        }

        AttributeInstance kb = p.getAttribute(Attribute.KNOCKBACK_RESISTANCE);
        if (kb != null) {
            kb.setBaseValue(0.0);
        }
    }
}
