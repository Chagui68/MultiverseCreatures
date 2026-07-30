package com.Chagui68.entities.boss.attack.ground;

import com.Chagui68.entities.BossInstance;
import com.Chagui68.entities.boss.attack.BossAttackBase;
import com.Chagui68.entities.boss.ArmorStandBoss;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

public class GroundSlamAttack extends BossAttackBase {
    public GroundSlamAttack(ArmorStandBoss boss) {
        super(boss);
    }

    @Override
    public void execute(BossInstance instance) {
        if (instance.isFlying) return;
        ArmorStand stand = instance.stand;
        Location center = stand.getLocation();
        World world = center.getWorld();

        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 2.0f, 0.5f);
        world.playSound(center, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.5f, 0.3f);

        for (int y = 20; y >= -3; y -= 1) {
            Location pl = center.clone().add(0, y, 0);
            world.spawnParticle(Particle.EXPLOSION, pl, 1, 0.2, 0.2, 0.2, 0);
            world.spawnParticle(Particle.FLAME, pl, 2, 0.2, 0.2, 0.2, 0.05);
            world.spawnParticle(Particle.CRIT, pl, 1, 0.3, 0.3, 0.3, 0.05);
        }

        for (int i = 0; i < 3; i++) {
            double r = 2.0 + i * 2.0;
            for (int a = 0; a < 30; a++) {
                double angle = (2 * Math.PI * a) / 30;
                double x = center.getX() + Math.cos(angle) * r;
                double z = center.getZ() + Math.sin(angle) * r;
                Location loc = new Location(world, x, center.getY(), z);
                world.spawnParticle(Particle.EXPLOSION, loc, 1, 0, 0, 0, 0);
                world.spawnParticle(Particle.CRIT, loc.clone().add(0, 0.5, 0), 2, 0.2, 0.2, 0.2, 0.05);
            }
        }

        world.spawnParticle(Particle.CLOUD, center.clone().add(0, 0.5, 0), 40, 3.0, 0.2, 3.0, 0.1);

        // Both arms swung forward/down for the slam. Z=0 keeps them alongside the
        // body rather than rotated backwards (the previous Z=180° / Z=45° values
        // made the arms point behind the stand and never reset).
        stand.setRightArmPose(new EulerAngle(Math.toRadians(-90), Math.toRadians(30), Math.toRadians(0)));
        stand.setLeftArmPose(new EulerAngle(Math.toRadians(-90), Math.toRadians(-15), Math.toRadians(0)));
        stand.setHeadPose(new EulerAngle(Math.toRadians(7), 0, 0));
        stand.setBodyPose(new EulerAngle(Math.toRadians(5), 0, 0));
        stand.setRightLegPose(new EulerAngle(Math.toRadians(15), 0, 0));
        stand.setLeftLegPose(new EulerAngle(Math.toRadians(-2), 0, 0));

        boss.skyPentagramAttack(instance);

        instance.shieldState = BossInstance.ShieldState.SLAM_DONE;
        instance.shieldTimer = 0;

        // Reset the slam pose shortly afterwards so the arms don't stay swung back.
        // Every other ground/aerial attack calls resetBossPose; GroundSlam was the
        // only one missing it, which left the stand frozen with arms pointing back.
        new BukkitRunnable() {
            @Override
            public void run() {
                boss.resetBossPose(instance);
            }
        }.runTaskLater(plugin, 50L);
    }

    @Override
    public String getName() {
        return "groundslam";
    }
}
