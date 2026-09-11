package com.maxwell.cataclysm_primed_soul.entity;

import com.maxwell.hyperdamagelib.util.DecayDamageUtil;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public final class EntityDamageHelper {
    public static final double ATTACK_RANGE_SCALE = 1.15D;

    private EntityDamageHelper() {
    }

    public static void hurtIgnoringInvulnerability(LivingEntity target, LivingEntity owner, float amount,
                                                      String deathMessage) {
        if (target == null || !target.isAlive() || amount <= 0.0F) {
            return;
        }
        DamageSource source = DecayDamageUtil.getPenetrateSource(target.level(), owner, deathMessage);
        DecayDamageUtil.applyCustomDamage(target, source, amount);
    }

    public static double expandRange(double range) {
        return range * ATTACK_RANGE_SCALE;
    }
}
