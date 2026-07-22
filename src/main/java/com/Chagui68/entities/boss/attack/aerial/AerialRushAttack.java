package com.Chagui68.entities.boss.attack.aerial;

import com.Chagui68.entities.BossInstance;
import com.Chagui68.entities.boss.attack.BossAttackBase;
import com.Chagui68.entities.boss.ArmorStandBoss;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.List;

public class AerialRushAttack extends BossAttackBase {
    public AerialRushAttack(ArmorStandBoss boss) {
        super(boss);
    }

    @Override
    public void execute(BossInstance instance) {
        ArmorStand stand = instance.stand;
        World world = stand.getWorld();
        Location center = stand.getLocation();

        new BukkitRunnable() {
            int t = 0;
            int dashCount = 0;
            Player currentTarget = null;

            @Override
            public void run() {
                if (stand.isDead() || !stand.isValid()) {
                    cancel();
                    return;
                }
                if (t < 15) {
                    double phase = (double) t / 15;
                    stand.setRightArmPose(new EulerAngle(Math.toRadians(-180 * phase), Math.toRadians(20 * phase), Math.toRadians(10 * phase)));
                    stand.setLeftArmPose(new EulerAngle(Math.toRadians(-180 * phase), Math.toRadians(-20 * phase), Math.toRadians(-10 * phase)));
                    stand.setBodyPose(new EulerAngle(Math.toRadians(10 * phase), 0, 0));
                    world.spawnParticle(Particle.END_ROD, center, 3, 1, 1, 1, 0.02);
                    if (t == 1) world.playSound(center, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.0f, 0.9f);
                } else if (dashCount < 5) {
                    stand.setRightArmPose(new EulerAngle(Math.toRadians(-180), Math.toRadians(20), Math.toRadians(10)));
                    stand.setLeftArmPose(new EulerAngle(Math.toRadians(-180), Math.toRadians(-20), Math.toRadians(-10)));
                    stand.setBodyPose(new EulerAngle(Math.toRadians(20), 0, 0));
                    stand.setHeadPose(new EulerAngle(Math.toRadians(30), 0, 0));
                    if (currentTarget == null || currentTarget.isDead() || !currentTarget.isOnline()) {
                        List<Player> targets = boss.getValidPlayersNear(center, 10000);
                        if (targets.isEmpty()) {
                            boss.resetBossPose(instance);
                            cancel();
                            return;
                        }
                        currentTarget = targets.get(random.nextInt(targets.size()));
                    }
                    Location targetLoc = currentTarget.getLocation();
                    Vector dir = targetLoc.toVector().subtract(stand.getLocation().toVector());
                    double dist = dir.length();
                    if (dist > 3) {
                        dir.normalize();
                        Location newLoc = stand.getLocation().add(dir.multiply(2.5));
                        newLoc.setY(targetLoc.getY() + 5);
                        stand.teleport(newLoc);
                        Location between = stand.getLocation();
                        world.spawnParticle(Particle.CLOUD, between, 10, 0.5, 0.3, 0.5, 0.05);
                        world.spawnParticle(Particle.CRIT, between, 8, 0.5, 0.5, 0.5, 0.05);
                        world.playSound(between, Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 0.8f);
                    } else {
                        targetLoc.getWorld().spawnParticle(Particle.EXPLOSION, targetLoc.clone().add(0, 1, 0), 10, 1, 0.5, 1, 0);
                        targetLoc.getWorld().playSound(targetLoc, Sound.ENTITY_PLAYER_ATTACK_STRONG, 1.2f, 0.7f);
                        double dmg = sealDamage * 0.7;
                        currentTarget.damage(dmg);
                        currentTarget.setVelocity(new Vector(0, 0.8, 0));
                        currentTarget.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 2));
                        currentTarget = null;
                        dashCount++;
                    }
                } else {
                    boss.resetBossPose(instance);
                    cancel();
                }
                t++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    @Override
    public String getName() {
        return "aerialrush";
    }
}
