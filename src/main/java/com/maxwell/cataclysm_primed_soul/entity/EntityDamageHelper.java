package com.maxwell.cataclysm_primed_soul.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public final class EntityDamageHelper {
    public static final double ATTACK_RANGE_SCALE = 1.15D;

    private EntityDamageHelper() {
    }

    public static boolean hurtIgnoringInvulnerability(LivingEntity target, DamageSource source, float amount) {
        if (target == null || !target.isAlive() || amount <= 0.0F) {
            return false;
        }
        target.invulnerableTime = 0;
        return target.hurt(source, amount);
    }

    public static double expandRange(double range) {
        return range * ATTACK_RANGE_SCALE;
    }
}
