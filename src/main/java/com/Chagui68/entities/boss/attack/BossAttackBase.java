package com.Chagui68.entities.boss.attack;

import com.Chagui68.MultiverseCreatures;
import com.Chagui68.entities.boss.ArmorStandBoss;

import java.util.Random;

public abstract class BossAttackBase implements BossAttack {
    protected final ArmorStandBoss boss;
    protected final MultiverseCreatures plugin;
    protected final Random random = new Random();
    protected final double sealDamage;
    protected final double hoverBarrageDamage;

    public BossAttackBase(ArmorStandBoss boss) {
        this.boss = boss;
        this.plugin = boss.getPlugin();
        this.sealDamage = boss.getSealDamage();
        this.hoverBarrageDamage = boss.getHoverBarrageDamage();
    }
}
