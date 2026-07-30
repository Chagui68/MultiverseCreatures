package com.Chagui68.listener;

import com.Chagui68.items.armor.EightHandledWheel;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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

    private static final NamespacedKey CHARGES_KEY = new NamespacedKey("multiversecreatures", "msc_wheel_charges");

    // Per-cause immunity windows for each player.
    //   blockUntil   : the damage of this cause is cancelled (true immunity).
    //   reTriggerUntil: a new charge cannot be consumed for this cause until this time.
    private static final class CauseState {
        long blockUntil;
        long reTriggerUntil;
    }

    private final Plugin plugin;
    private final Map<UUID, Map<EntityDamageEvent.DamageCause, CauseState>> playerStates = new ConcurrentHashMap<>();
    private final Set<UUID> regenActive = ConcurrentHashMap.newKeySet();

    public EightHandledWheelHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    private boolean isWheel(ItemStack item) {
        if (item == null || item.getType() != Material.NETHERITE_HELMET) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(EightHandledWheel.WHEEL_KEY, PersistentDataType.INTEGER);
    }

    private int getCharges(ItemStack helm) {
        ItemMeta meta = helm.getItemMeta();
        if (meta == null) return EightHandledWheel.MAX_CHARGES;
        return meta.getPersistentDataContainer().getOrDefault(CHARGES_KEY, PersistentDataType.INTEGER, EightHandledWheel.MAX_CHARGES);
    }

    private void setCharges(ItemStack helm, int charges) {
        ItemMeta meta = helm.getItemMeta();
        if (meta == null) return;
        if (charges >= EightHandledWheel.MAX_CHARGES) {
            meta.getPersistentDataContainer().remove(CHARGES_KEY);
        } else {
            meta.getPersistentDataContainer().set(CHARGES_KEY, PersistentDataType.INTEGER, charges);
        }
        helm.setItemMeta(meta);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player p)) return;
        ItemStack helm = p.getInventory().getHelmet();
        if (!isWheel(helm)) return;

        UUID uuid = p.getUniqueId();
        long now = System.currentTimeMillis();
        EntityDamageEvent.DamageCause cause = event.getCause();

        Map<EntityDamageEvent.DamageCause, CauseState> causeMap = playerStates.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());
        CauseState state = causeMap.get(cause);

        // Phase 1: still inside the immunity window for this damage type.
        // Block ALL incoming damage of this type — not just the first hit.
        if (state != null && state.blockUntil > now) {
            event.setCancelled(true);
            return;
        }

        // Phase 2: between the end of immunity and the end of the re-trigger cooldown,
        // damage passes through normally and NO new charge is consumed.
        if (state != null && state.reTriggerUntil > now) {
            return;
        }

        // Phase 3: the cooldown has fully elapsed — a new adaptation can trigger.
        int c = getCharges(helm);
        if (c <= 0) return;

        setCharges(helm, c - 1);

        CauseState s = state != null ? state : new CauseState();
        s.blockUntil = now + (EightHandledWheel.BLOCK_DURATION_TICKS * 50L); // ticks -> ms
        s.reTriggerUntil = now + EightHandledWheel.BLOCK_COOLDOWN_MS;
        causeMap.put(cause, s);

        event.setCancelled(true);
        p.sendMessage(ChatColor.GRAY + "Wheel adapts to " + cause.name() + " (" + (c - 1) + " charges remain)");
        p.getWorld().playSound(p.getLocation(), org.bukkit.Sound.BLOCK_BEACON_ACTIVATE, 0.5f, 1.5f);
        p.getWorld().spawnParticle(org.bukkit.Particle.END_ROD, p.getLocation().add(0, 1, 0), 15, 0.3, 0.5, 0.3, 0.05);

        if (c - 1 < EightHandledWheel.MAX_CHARGES && regenActive.add(uuid)) {
            startRegen(uuid);
        }
    }

    private void startRegen(UUID uuid) {
        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                Player p = org.bukkit.Bukkit.getPlayer(uuid);
                if (p == null || !p.isOnline()) {
                    playerStates.remove(uuid);
                    cancel();
                    regenActive.remove(uuid);
                    return;
                }
                ItemStack helm = p.getInventory().getHelmet();
                if (!isWheel(helm)) {
                    playerStates.remove(uuid);
                    cancel();
                    regenActive.remove(uuid);
                    return;
                }
                int c = getCharges(helm);
                if (c >= EightHandledWheel.MAX_CHARGES) {
                    playerStates.remove(uuid);
                    cancel();
                    regenActive.remove(uuid);
                    return;
                }
                setCharges(helm, c + 1);
            }
        }.runTaskTimer(plugin, EightHandledWheel.CHARGE_REGEN_TICKS, EightHandledWheel.CHARGE_REGEN_TICKS);
    }
}
