package com.maxwell.cataclysm_primed_soul.entity;

import com.maxwell.hyperdamagelib.util.DecayDamageUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public final class EntityDamageHelper {
    public static final double ATTACK_RANGE_SCALE = 1.15D;

    private EntityDamageHelper() {
    }

    public static boolean hurtIgnoringInvulnerability(LivingEntity target, LivingEntity owner, float amount,
                                                      String deathMessage) {
        if (target == null || !target.isAlive() || amount <= 0.0F) {
            return false;
        }
        target.invulnerableTime = 0;
        DamageSource source = DecayDamageUtil.getPenetrateSource(target.level(), owner, deathMessage);
        return target.hurt(source, amount);
    }

    public static double expandRange(double range) {
        return range * ATTACK_RANGE_SCALE;
    }
}
